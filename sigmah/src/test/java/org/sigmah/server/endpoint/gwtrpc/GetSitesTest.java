/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.SortInfo;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.command.GetSitePoints;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.SitePointList;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.test.InjectionSupport;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetSitesTest extends CommandTestCase {
    private static final int DATABASE_OWNER = 1;
                

    @Test
    public void testActivityQueryBasic() throws CommandException {


        setUser(DATABASE_OWNER);

        GetSites cmd = new GetSites();
        cmd.filter().onActivity(1);
        cmd.setSortInfo(new SortInfo("date2", SortDir.DESC));

        PagingLoadResult<SiteDTO> result = execute(cmd);    

        Assert.assertEquals("totalLength", 3, result.getData().size());
        Assert.assertEquals("totalLength", 3, result.getTotalLength());
        Assert.assertEquals("offset", 0, result.getOffset());
        //Assert.assertNull("row(0).activity", result.getData().get(0).getActivity());

        // assure sorted
        Assert.assertEquals("sorted", 2, result.getData().get(0).getId());
        Assert.assertEquals("sorted", 1, result.getData().get(1).getId());
        Assert.assertEquals("sorted", 3, result.getData().get(2).getId());

        // assure indicators are present (site id=3)
        SiteDTO s = result.getData().get(2);

        Assert.assertEquals("entityName", "Ituri", s.getAdminEntity(1).getName());
        Assert.assertNotNull("admin bounds", s.getAdminEntity(1).getBounds());
        Assert.assertEquals("indicator", 10000.0, s.getIndicatorValue(1));
    }

    @Test
    public void testIndicatorSort() throws CommandException {

        setUser(DATABASE_OWNER);

        GetSites cmd = new GetSites();
        cmd.filter().onActivity(1);
        cmd.setSortInfo(new SortInfo(IndicatorDTO.getPropertyName(1), SortDir.DESC));

        PagingLoadResult<SiteDTO> result = execute(cmd);

        // assure sorted
        Assert.assertEquals("sorted", 10000.0, result.getData().get(0).getIndicatorValue(1));
        Assert.assertEquals("sorted", 3600.0, result.getData().get(1).getIndicatorValue(1));
        Assert.assertEquals("sorted", 1500.0, result.getData().get(2).getIndicatorValue(1));

        Assert.assertNotNull("activityId", result.getData().get(0).getActivityId());
    }


    @Test
    public void testActivityQueryPaged() throws CommandException {

        setUser(DATABASE_OWNER);

        GetSites cmd = new GetSites();
        cmd.filter().onActivity(1);
        cmd.setSortInfo(new SortInfo(IndicatorDTO.getPropertyName(1), SortDir.DESC));
        cmd.setLimit(2);
        cmd.setOffset(0);

        PagingLoadResult<SiteDTO> result = execute(cmd);

        Assert.assertEquals("rows retrieved [0,2]", 2, result.getData().size());
        Assert.assertEquals("total rows [0,2]", 3, result.getTotalLength());

        cmd.setOffset(1);
        cmd.setLimit(2);

        result = execute(cmd);

        Assert.assertEquals("offset [1,2]", 1, result.getOffset());
        Assert.assertEquals("rows retrieved [1,2]", 2, result.getData().size());
        Assert.assertEquals("total rows [1,2]", 3, result.getTotalLength());

        cmd.setOffset(0);
        cmd.setLimit(50);

        result = execute(cmd);

        Assert.assertEquals("offset [0,50]", 0, result.getOffset());
        Assert.assertEquals("rows retrieved [0,50]", 3, result.getData().size());
        Assert.assertEquals("total rows [0,50]", 3, result.getTotalLength());

    }


    @Test
    public void testDatabase() throws CommandException {

        setUser(DATABASE_OWNER);

        GetSites cmd = new GetSites();
        cmd.filter().addRestriction(DimensionType.Database, 2);

        PagingLoadResult<SiteDTO> result = execute(cmd);

        Assert.assertEquals("rows", 3, result.getData().size());
        Assert.assertNotNull("activityId", result.getData().get(0).getActivityId());

    }

    @Test
    public void testDatabasePaged() throws CommandException {

        setUser(DATABASE_OWNER);

        GetSites cmd = new GetSites();
        cmd.getFilter().addRestriction(DimensionType.Database, 1);
        cmd.setLimit(2);

        PagingLoadResult<SiteDTO> result = execute(cmd);


        Assert.assertEquals("rows", 2, result.getData().size());

    }

    @Test
    public void testDatabasePartner2PartnerVisibility() throws CommandException {

        setUser(2); // BAVON (can't see other partner's stuff)

        GetSites cmd = new GetSites();
        cmd.getFilter().addRestriction(DimensionType.Database, 1);

        PagingLoadResult<SiteDTO> result = execute(cmd);

        Assert.assertEquals("rows", 3, result.getData().size());
    }

    @Test
    public void testAll() throws CommandException {

        setUser(DATABASE_OWNER);

        GetSites cmd = new GetSites();

        PagingLoadResult<SiteDTO> result = execute(cmd);

        Assert.assertEquals("rows", 8, result.getData().size());
        Assert.assertNotNull("activityId", result.getData().get(0).getActivityId());

    }

    @Test
    public void testAllWithRemovedUser() throws CommandException {

        setUser(5); // Christian (Bad guy!)

        PagingLoadResult<SiteDTO> result = execute(new GetSites());

        Assert.assertEquals("rows", 0, result.getData().size());

    }

    @Test
    public void testSeekSite() throws Exception {

        setUser(DATABASE_OWNER);

        GetSites cmd = new GetSites();
        cmd.filter().onActivity(1);
        cmd.setSortInfo(new SortInfo(IndicatorDTO.getPropertyName(1), SortDir.DESC));
        cmd.setLimit(2);
        cmd.setSeekToSiteId(1);

        PagingLoadResult<SiteDTO> result = execute(cmd);

        Assert.assertEquals("second page returned", 2, result.getOffset());
        Assert.assertEquals("rows on this page", 1, result.getData().size());
        Assert.assertEquals("correct site returned", 1, result.getData().get(0).getId());
    }
    
    @Test
    public void testSitePointsForIndicator() throws Exception {
    	setUser(DATABASE_OWNER);
    	
    	Filter filter = new Filter();
    	filter.addRestriction(DimensionType.Indicator, 1);
    	
    	GetSitePoints cmd = new GetSitePoints(filter);
    	
    	SitePointList list = execute(cmd);
    	Assert.assertEquals(3, list.getPoints().size());
    }
}
