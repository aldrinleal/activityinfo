package org.sigmah.shared.dao.pivot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dao.pivot.bundler.Bundler;
import org.sigmah.shared.dao.pivot.bundler.EntityBundler;
import org.sigmah.shared.dao.pivot.bundler.MonthBundler;
import org.sigmah.shared.dao.pivot.bundler.OrderedEntityBundler;
import org.sigmah.shared.dao.pivot.bundler.QuarterBundler;
import org.sigmah.shared.dao.pivot.bundler.SimpleBundler;
import org.sigmah.shared.dao.pivot.bundler.SiteCountBundler;
import org.sigmah.shared.dao.pivot.bundler.SumAndAverageBundler;
import org.sigmah.shared.dao.pivot.bundler.YearBundler;
import org.sigmah.shared.report.model.AdminDimension;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;

import com.bedatadriven.rebar.sql.client.SqlResultCallback;
import com.bedatadriven.rebar.sql.client.SqlResultSet;
import com.bedatadriven.rebar.sql.client.SqlResultSetRow;
import com.bedatadriven.rebar.sql.client.SqlTransaction;
import com.bedatadriven.rebar.sql.client.query.SqlDialect;
import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PivotQuery {
	private final Filter filter;
	private final Set<Dimension> dimensions;
	private final int userId;
	private final SqlDialect dialect;
	
	private final SqlTransaction tx;
	
	private List<Bucket> buckets;	
	private final SqlQuery query = new SqlQuery();
	private final List<Bundler> bundlers = new ArrayList<Bundler>();
	
	private AsyncCallback<List<Bucket>> callback = null;

	
	
	private int nextColumnIndex = 1;
	
    public PivotQuery(SqlTransaction tx, SqlDialect dialect, Filter filter, Set<Dimension> dimensions, int userId) {
		super();
		this.filter = filter;
		this.dimensions = dimensions;
		this.userId = userId;
		this.dialect = dialect;
		this.tx = tx;
	}
    
	public PivotQuery addTo(List<Bucket> buckets) {
		this.buckets = buckets;
		return this;
	}
    
    
    public PivotQuery callbackTo(AsyncCallback<List<Bucket>> callback) {
    	this.callback = callback;
    	return this;
    }
    
    private String appendColumn(String expr) {
    	String alias = "c" + (nextColumnIndex++);
    	query.appendColumn(expr, alias);
    	return alias;
    }
    
    private String appendDimColumn(String expr) {
		query.groupBy(expr);
		return appendColumn(expr);
	}

    public void queryForSumAndAverages() {
        /* We're just going to go ahead and add all the tables we need to the SQL statement;
        * this saves us some work and hopefully the SQL server will optimize out any unused
        * tables
        */
    	query.from(" IndicatorValue V " +
                "LEFT JOIN ReportingPeriod Period ON (Period.ReportingPeriodId=V.ReportingPeriodId) " +
                "LEFT JOIN Indicator ON (Indicator.IndicatorId = V.IndicatorId) " +
                "LEFT JOIN Site ON (Period.SiteId = Site.SiteId) " +
                "LEFT JOIN Partner ON (Site.PartnerId = Partner.PartnerId)" +
                "LEFT JOIN Project ON (Site.ProjectId = Project.ProjectId) " +
                "LEFT JOIN Location ON (Location.LocationId = Site.LocationId) " +
                "LEFT JOIN Activity ON (Activity.ActivityId = Site.ActivityId) " +
                "LEFT JOIN UserDatabase ON (Activity.DatabaseId = UserDatabase.DatabaseId) ");
        /*
         * First add the indicator to the query: we can't aggregate values from different
         * indicators so this is a must
         */
    	String aggregationTypeAlias = appendColumn("Indicator.Aggregation");
    	String sumAlias = appendColumn("SUM(V.Value)");
    	String avgAlias = appendColumn("AVG(V.Value)");
    	
    	query.groupBy("V.IndicatorId");
    	query.groupBy("Indicator.Aggregation");
    	query.whereTrue(" ((V.value <> 0 and Indicator.Aggregation=0) or Indicator.Aggregation=1) ");
        
    	bundlers.add( new SumAndAverageBundler(aggregationTypeAlias, sumAlias, avgAlias) );
       
    	buildAndExecuteRestOfQuery();
    }

    public void queryForSiteCounts() {

        /* We're just going to go ahead and add all the tables we need to the SQL statement;
        * this saves us some work and hopefully the SQL server will optimze out any unused
        * tables
        */

        query.from(" Site " +
                "LEFT JOIN Partner ON (Site.PartnerId = Partner.PartnerId) " +
                "LEFT JOIN Project ON (Site.ProjectId = Project.ProjectId) " +
                "LEFT JOIN Location ON (Location.LocationId = Site.LocationId) " +
                "LEFT JOIN Activity ON (Activity.ActivityId = Site.ActivityId) " +
                "LEFT JOIN Indicator ON (Indicator.ActivityId = Activity.ActivityId) " +
                "LEFT JOIN UserDatabase ON (Activity.DatabaseId = UserDatabase.DatabaseId) " +
                "LEFT JOIN ReportingPeriod Period ON (Period.SiteId = Site.SiteId) ");

        /* First add the indicator to the query: we can't aggregate values from different
        * indicators so this is a must
        *
        */
        String count = appendColumn("COUNT(DISTINCT Site.SiteId)");
        query.groupBy("Indicator.IndicatorId");
        query.whereTrue("Indicator.Aggregation=2 and not Period.Monitoring ");

        bundlers.add(new SiteCountBundler(count));

        buildAndExecuteRestOfQuery();
    }


    protected void buildAndExecuteRestOfQuery() {
        /* Now add any other dimensions  */
        for (Dimension dimension : dimensions) {

            if (dimension.getType() == DimensionType.Activity) {
            	addOrderedEntityDimension(dimension, "Site.ActivityId", "Activity.Name", "Activity.SortOrder");

            } else if (dimension.getType() == DimensionType.ActivityCategory) {
                addSimpleDimension(dimension, "Activity.Category");

            } else if (dimension.getType() == DimensionType.Database) {
            	addEntityDimension(dimension, "Activity.DatabaseId", "UserDatabase.Name");

            } else if (dimension.getType() == DimensionType.Partner) {
            	addEntityDimension(dimension, "Site.PartnerId", "Partner.Name");
                
            } else if (dimension.getType() == DimensionType.Project) {
            	addEntityDimension(dimension, "Site.ProjectId", "Project.Name");
              
            } else if (dimension.getType() == DimensionType.Location) {
            	addEntityDimension(dimension, "Site.LocationId", "Location.Name");

            } else if (dimension.getType() == DimensionType.Indicator) {
            	addOrderedEntityDimension(dimension, "Indicator.IndicatorId", "Indicator.Name", "Indicator.SortOrder");

            } else if (dimension.getType() == DimensionType.IndicatorCategory) {
            	addSimpleDimension(dimension, "Indicator.Category");

            } else if (dimension instanceof DateDimension) {
                DateDimension dateDim = (DateDimension) dimension;

                if (dateDim.getUnit() == DateUnit.YEAR) {
                	String yearAlias = appendDimColumn(dialect.yearFunction("Period.Date2"));

                    bundlers.add(new YearBundler(dimension, yearAlias));

                } else if (dateDim.getUnit() == DateUnit.MONTH) {
                	String yearAlias = appendDimColumn(dialect.yearFunction("Period.Date2"));
                	String monthAlias = appendDimColumn(dialect.monthFunction("Period.Date2"));
                	
                    bundlers.add(new MonthBundler(dimension, yearAlias, monthAlias));

                } else if (dateDim.getUnit() == DateUnit.QUARTER) {
                	String yearAlias = appendDimColumn(dialect.yearFunction("Period.Date2"));
                	String quarterAlias = appendDimColumn(dialect.quarterFunction("Period.Date2"));

                	bundlers.add(new QuarterBundler(dimension, yearAlias, quarterAlias));
                    nextColumnIndex += 2;
                }

            } else if (dimension instanceof AdminDimension) {
                AdminDimension adminDim = (AdminDimension) dimension;

                String tableAlias = "AdminLevel" + adminDim.getLevelId();

                query.from(new StringBuilder(" LEFT JOIN " +
                        "(SELECT L.LocationId, E.AdminEntityId, E.Name " +
                        "FROM LocationAdminLink L " +
                        "LEFT JOIN AdminEntity E ON (L.AdminEntityId=E.AdminEntityID) " +
                        "WHERE E.AdminLevelId=").append(adminDim.getLevelId())
                        .append(") AS ").append(tableAlias)
                        .append(" ON (Location.LocationId=").append(tableAlias).append(".LocationId)").toString());

                addEntityDimension(dimension, tableAlias + ".AdminEntityId", tableAlias + ".Name");
                
            
            } /*else if (dimension instanceof AttributeGroupDimension) {
            	AttributeGroupDimension attrGroupDim = (AttributeGroupDimension) dimension;
            	List < Integer > attributeIds = queryAttributeIds(attrGroupDim);
            	int count = 0;
            	for (Integer attributeId: attributeIds) {
            		String tableAlias = "Attribute" + attributeId;

                	from.append("LEFT JOIN " +
                			"(SELECT AttributeValue.SiteId, Attribute.Name as " + tableAlias + "val " +
                			"FROM AttributeValue " +
                			"LEFT JOIN  Attribute ON (Attribute.AttributeId = AttributeValue.AttributeId) " +
                			"WHERE AttributeValue.value AND Attribute.AttributeId = ")
                			.append(attributeId).append(") AS ").append(tableAlias).append(" ON (")
                			.append(tableAlias).append(".SiteId = Site.SiteId)");

                	dimColumns.append(", ").append(tableAlias).append(".").append(tableAlias).append("val ");
                	count++;
            	}
                Log.debug("Total attribute column count = " + count);

            	bundlers.add(new AttributeBundler(dimension, nextColumnIndex, count));
            	nextColumnIndex += count;

            }*/
        }


        /* And start on our where clause... */

        // don't include entities that have been deleted
        query.whereTrue("Site.dateDeleted is null and " +
                "Activity.dateDeleted is null and " +
                "Indicator.dateDeleted is null and " +
                "UserDatabase.dateDeleted is null ");

        // and only allow results that are visible to this user.
        if(!GWT.isClient()) {
        	appendVisibilityFilter();
        }


        if (filter.getMinDate() != null) {
            query.where("Period.date2").greaterThanOrEqualTo(filter.getMinDate());
        }
        if (filter.getMaxDate() != null) {
        	query.where("Period.date2").lessThanOrEqualTo(filter.getMinDate());
        }

        appendDimensionRestrictions();

        
        query.execute(tx, new SqlResultCallback() {
			
			@Override
			public void onSuccess(SqlTransaction tx, SqlResultSet results) {
				for(SqlResultSetRow row : results.getRows()) {
					Bucket bucket = new Bucket();
                    for (Bundler bundler : bundlers) {
                        bundler.bundle(row, bucket);
                    }
                    buckets.add(bucket);
				}
                if(callback != null) {
                	callback.onSuccess(buckets);
                }
			}
		});    
    }

	private void addEntityDimension(Dimension dimension, String id, String label) {
		String idAlias = appendDimColumn(id);
		String labelAlias = appendDimColumn(label);
		
		bundlers.add(new EntityBundler(dimension, idAlias, labelAlias));
	}


	private void addOrderedEntityDimension(Dimension dimension, String id, String label, String sortOrder) {
		String idAlias = appendDimColumn(id);
		String labelAlias = appendDimColumn(label);
		String sortOrderAlias = appendDimColumn(sortOrder);
		
		bundlers.add(new OrderedEntityBundler(dimension, idAlias, labelAlias, sortOrderAlias));
	}

	private void addSimpleDimension(Dimension dimension, String label) {
		String labelAlias = appendDimColumn(label);
		bundlers.add(new SimpleBundler(dimension, labelAlias));
	}	

	private void appendVisibilityFilter() {
        StringBuilder filter = new StringBuilder();
        filter.append("(UserDatabase.OwnerUserId = ").append(userId).append(" OR ")
             .append(userId).append(" in (select p.UserId from UserPermission p " +
                "where p.AllowView and " +
                "p.UserId=").append(userId).append(" AND p.DatabaseId = UserDatabase.DatabaseId))");
        
        query.whereTrue(filter.toString());
    }

    private void appendDimensionRestrictions() {
        for (DimensionType type : filter.getRestrictedDimensions()) {
            if (type == DimensionType.Indicator) {
            	query.where("Indicator.IndicatorId").in(filter.getRestrictions(DimensionType.Indicator));
            } else if (type == DimensionType.Activity) {
            	query.where("Site.ActivityId").in(filter.getRestrictions(DimensionType.Activity));
            } else if (type == DimensionType.Database) {
            	query.where("Activity.DatabaseId").in(filter.getRestrictions(DimensionType.Database));
            } else if (type == DimensionType.Partner) {
            	query.where("Site.PartnerId").in(filter.getRestrictions(DimensionType.Partner));
            } else if (type == DimensionType.Project) {
            	query.where("Site.ProjectId").in(filter.getRestrictions(DimensionType.Project));
            } else if (type == DimensionType.Location) {
            	query.where("Site.LocationId").in(filter.getRestrictions(DimensionType.Location));
            } else if (type == DimensionType.AdminLevel) {
            	query.where("Site.LocationId").in(
            			SqlQuery.select("Link.LocationId").from("LocationAdminLink", "Link")
            				.where("Link.AdminEntityId").in(filter.getRestrictions(DimensionType.AdminLevel)));
            }
        }
    }
}