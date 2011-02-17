/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.domain;

// Generated Apr 9, 2009 7:58:20 AM by Hibernate Tools 3.2.2.GA

import javax.persistence.*;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Alex Bertram
 *
 */
@Entity
public class ReportingPeriod implements java.io.Serializable, Deleteable {

	private static final long serialVersionUID = 5691930618952633225L;
    private int id;
    private Site site;
	private boolean monitoring;
	private Date date1;
	private Date date2;
	private String comments;
	
	private Date dateCreated;
	private Date dateEdited;
	private Date dateDeleted;
	
//	private Map<Integer, Double> indicatorValues = new HashMap<Integer, Double>(0);

	private Set<IndicatorValue> indicatorValues = new HashSet<IndicatorValue>(0);
	
	public ReportingPeriod() {
		
		Date now = new Date();
		setDateCreated(now);
		setDateEdited(now);
	}

    public ReportingPeriod(Site site) {
        this();
        setSite(site);
    }


    @Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ReportingPeriodId", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SiteId", nullable = false)
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	@Column(name = "Monitoring", nullable = false)
	public boolean isMonitoring() {
		return this.monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "Date1", nullable = false, length = 23)
	public Date getDate1() {
		return this.date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "Date2", nullable = false, length = 23)
	public Date getDate2() {
		return this.date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

    @Lob
	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DateCreated", nullable = false, length = 23)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DateEdited", nullable = false, length = 23)
	public Date getDateEdited() {
		return this.dateEdited;
	}

	public void setDateEdited(Date dateEdited) {
		this.dateEdited = dateEdited;
	}

//	/**
//	 * 
//	 * @return
//	 * @see https://forum.hibernate.org/viewtopic.php?t=953403
//	 */
//	@org.hibernate.annotations.CollectionOfElements
//    @JoinTable(name="IndicatorValue", joinColumns={@JoinColumn(name="reportingPeriodId")})
//	@org.hibernate.annotations.MapKey(columns={@Column(name="indicatorId", nullable=false)})
//	@Column(name="Value", nullable=true, columnDefinition="REAL")   
//	public Map<Integer, Double> getIndicatorValues() {
//		return indicatorValues;
//	}
//	
//	public void setIndicatorValues(Map<Integer,Double> values) {
//		this.indicatorValues = values;
//	}
	
	@OneToMany(mappedBy="reportingPeriod", fetch=FetchType.LAZY)
    @org.hibernate.annotations.Filter(
			name="hideDeleted",
			condition="(IndicatorId not in (select i.IndicatorId from Indicator i where i.dateDeleted is not null))")
	public Set<IndicatorValue> getIndicatorValues() {
		return indicatorValues;
	}
	
	public void setIndicatorValues(Set<IndicatorValue> values) {
		this.indicatorValues = values;
	}

    
	
	@Override
	public boolean equals(Object other) {
		if(this==other) {
            return true;
        }
		if(other==null) {
            return false;
        }
		if(!(other instanceof ReportingPeriod)) {
            return false;
        }
	
		ReportingPeriod that = (ReportingPeriod)other;
		
		return this.getId() == that.getId();
	}
	
	@Column
	@Temporal(value=TemporalType.TIMESTAMP)
	protected Date getDateDeleted() {
		return this.dateDeleted;
	}
	
	protected void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}

	public void delete() {
		setDateDeleted(new Date());
	}


	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() == null;
	}
	
}
