package opca.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class Summary {
	@Id
	private Integer opinionId;
    @OneToOne(fetch=FetchType.LAZY) @MapsId
	private SlipOpinion slipOpinion; 
	
    @Column(columnDefinition="varchar(63)")
    private String trialCourtCase;
    @Column(columnDefinition="varchar(31)")
    private String division;
    @Column(columnDefinition="varchar(15)")
    private String caseType;
    private Date filingDate;
    private Date completionDate;
    @ElementCollection
    private Set<String> crossReferencedCases;

	public Summary() {}
	public Summary(SlipOpinion slipOpinion, String trialCourtCase, String division, String caseType,
			Date filingDate, Date completionDate, Set<String> crossReferencedCases) {
		setSlipOpinion(slipOpinion);
		setTrialCourtCase(trialCourtCase);
		setDivision(division);
		setCaseType(caseType);
		setFilingDate(filingDate);
		setCompletionDate(completionDate);
		setCrossReferencedCases(crossReferencedCases);
	}
	public Integer getOpinionId() {
		return opinionId;
	}
	public void setOpinionId(Integer opinionId) {
		this.opinionId = opinionId;
	}
	public SlipOpinion getSlipOpinion() {
		return slipOpinion;
	}
	public void setSlipOpinion(SlipOpinion slipOpinion) {
		this.slipOpinion = slipOpinion;
	}
	public String getTrialCourtCase() {
		return trialCourtCase;
	}
	public void setTrialCourtCase(String trialCourtCase) {
		if ( trialCourtCase != null && trialCourtCase.length() > 63 ) trialCourtCase = trialCourtCase.substring(0, 63);
		this.trialCourtCase = trialCourtCase;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		if ( division != null && division.length() > 31 ) division = division.substring(0, 31);
		this.division = division;
	}
	public String getCaseType() {
		return caseType;
	}
	public void setCaseType(String caseType) {
		if ( caseType != null && caseType.length() > 15 ) caseType = caseType.substring(0, 15);
		this.caseType = caseType;
	}
	public Date getFilingDate() {
		return filingDate;
	}
	public void setFilingDate(Date filingDate) {
		this.filingDate = filingDate;
	}
	public Date getCompletionDate() {
		return completionDate;
	}
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	public Set<String> getCrossReferencedCases() {
		return crossReferencedCases;
	}
	public void setCrossReferencedCases(Set<String> crossReferencedCases) {
		this.crossReferencedCases = crossReferencedCases;
	}
}
