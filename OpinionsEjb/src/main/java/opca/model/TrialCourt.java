package opca.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class TrialCourt {
	@Id
	private Integer opinionId;
    @OneToOne(fetch=FetchType.LAZY) @MapsId
	private SlipOpinion slipOpinion; 

    @Column(columnDefinition="varchar(127)")
    private String trialCourtName; 
    @Column(columnDefinition="varchar(127)")
    private String county; 
    @Column(columnDefinition="varchar(63)")
    private String trialCourtCaseNumber; 
    @Column(columnDefinition="varchar(127)")
    private String trialCourtJudge; 
    private Date trialCourtJudgmentDate;

    public TrialCourt() {}
    public TrialCourt(Integer opinionId, SlipOpinion slipOpinion, String trialCourtName, String county,
			String trialCourtCaseNumber, String trialCourtJudge, Date trialCourtJudgmentDate) {
		this.slipOpinion = slipOpinion;
		this.trialCourtName = trialCourtName;
		this.county = county;
		this.trialCourtCaseNumber = trialCourtCaseNumber;
		this.trialCourtJudge = trialCourtJudge;
		this.trialCourtJudgmentDate = trialCourtJudgmentDate;
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
	public String getTrialCourtName() {
		return trialCourtName;
	}
	public void setTrialCourtName(String trialCourtName) {
		if ( trialCourtName != null && trialCourtName.length() > 127 ) trialCourtName = trialCourtName.substring(0, 127);
		this.trialCourtName = trialCourtName;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		if ( trialCourtName != null && trialCourtName.length() > 63 ) trialCourtName = trialCourtName.substring(0, 63);
		this.county = county;
	}
	public String getTrialCourtCaseNumber() {
		return trialCourtCaseNumber;
	}
	public void setTrialCourtCaseNumber(String trialCourtCaseNumber) {
		if ( trialCourtName != null && trialCourtName.length() > 127 ) trialCourtName = trialCourtName.substring(0, 127);
		this.trialCourtCaseNumber = trialCourtCaseNumber;
	}
	public String getTrialCourtJudge() {
		return trialCourtJudge;
	}
	public void setTrialCourtJudge(String trialCourtJudge) {
		if ( trialCourtName != null && trialCourtName.length() > 127 ) trialCourtName = trialCourtName.substring(0, 127);
		this.trialCourtJudge = trialCourtJudge;
	}
	public Date getTrialCourtJudgmentDate() {
		return trialCourtJudgmentDate;
	}
	public void setTrialCourtJudgmentDate(Date trialCourtJudgmentDate) {
		this.trialCourtJudgmentDate = trialCourtJudgmentDate;
	} 
}
