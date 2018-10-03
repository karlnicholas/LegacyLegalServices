package opca.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(name="SlipProperties.findAll", 
		query="select p from SlipProperties p"), 
})
@Entity
@SuppressWarnings("serial")
public class SlipProperties implements Serializable {
	// does this space count? Don't think so, row allocation is dynamic anyway.
	@Id
	private Integer opinionId;
    @OneToOne(fetch=FetchType.LAZY) @MapsId
	private SlipOpinion slipOpinion; 
	@Column(columnDefinition="varchar(15)")
	private String court;
	@Column(columnDefinition = "varchar(31)")
    private String fileName;
	@Column(columnDefinition = "varchar(7)")
	private String fileExtension;
    @Column(columnDefinition="varchar(63)")
    private String trialCourtCase;
    @Column(columnDefinition="varchar(255)")
    private String caseCaption;    
    @Column(columnDefinition="varchar(31)")
    private String division;
    @Column(columnDefinition="varchar(15)")
    private String caseType;
    private Date filingDate;
    private Date completionDate;
    @Column(columnDefinition="varchar(127)")
	private String disposition; 
	private Date date; 
    @Column(columnDefinition="varchar(255)")
	private String dispositionDescription; 
    @Column(columnDefinition="varchar(31)")
	private String publicationStatus; 
    @Column(columnDefinition="varchar(63)")
	private String author; 
    @Column(columnDefinition="varchar(255)")
	private String participants; 
    @Column(columnDefinition="varchar(255)")
	private String caseCitation;
    @Column(columnDefinition="varchar(127)")
    private String trialCourtName; 
    @Column(columnDefinition="varchar(127)")
    private String county; 
    @Column(columnDefinition="varchar(63)")
    private String trialCourtCaseNumber; 
    @Column(columnDefinition="varchar(127)")
    private String trialCourtJudge; 
    private Date trialCourtJudgmentDate;
    @OneToMany(mappedBy="slipProperties")
    private Set<PartyAttorneyPair> partyAttorneyPairs;

	public SlipProperties() {}
	public SlipProperties(SlipOpinion slipOpinion) {
		this.slipOpinion = slipOpinion;
	}
	public SlipProperties(SlipOpinion slipOpinion, String fileName, String fileExtension, String court) {
		this.slipOpinion = slipOpinion;
    	setFileName(fileName);
    	setFileExtension(fileExtension);
		setCourt(court);
    }

	public SlipProperties(SlipOpinion slipOpinion, SlipOpinion slipCopy) {
		this.slipOpinion = slipOpinion;
    	setFileName(slipCopy.getFileName());
    	setFileExtension(slipCopy.getFileExtension());
		setCourt(slipCopy.getCourt());
	}
    public Integer getOpinionKey() {
		return opinionId;
	}
	public void setOpinionKey(Integer opinionId) {
		this.opinionId = opinionId;
	}
	public SlipOpinion getSlipOpinion() {
		return slipOpinion;
	}
	public void setSlipOpinion(SlipOpinion slipOpinion) {
		this.slipOpinion = slipOpinion;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		if ( fileName != null && fileName.length() > 31 ) fileName = fileName.substring(0, 31);
		this.fileName = fileName;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		if ( fileExtension != null && fileExtension.length() > 7 ) fileExtension = fileExtension.substring(0, 7);
		this.fileExtension = fileExtension;
	}
	public String getCourt() {
		return court;
	}
	public void setCourt(String court) {
		if ( court != null && court.length() > 15 ) court = court.substring(0, 15);
		this.court = court;
	}
	public String getTrialCourtCase() {
		return trialCourtCase;
	}
	public void setTrialCourtCase(String trialCourtCase) {
		if ( trialCourtCase != null && trialCourtCase.length() > 63 ) trialCourtCase = trialCourtCase.substring(0, 63);
		this.trialCourtCase = trialCourtCase;
	}
	public String getCaseCaption() {
		return caseCaption;
	}
	public void setCaseCaption(String caseCaption) {
		if ( caseCaption != null && caseCaption.length() > 255 ) caseCaption = caseCaption.substring(0, 255);
		this.caseCaption = caseCaption;
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
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		if ( disposition != null && disposition.length() > 127 ) disposition = disposition.substring(0, 127);
		this.disposition = disposition;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDispositionDescription() {
		return dispositionDescription;
	}
	public void setDispositionDescription(String dispositionDescription) {
		if ( dispositionDescription != null && dispositionDescription.length() > 255 ) dispositionDescription = dispositionDescription.substring(0, 255);
		this.dispositionDescription = dispositionDescription;
	}
	public String getPublicationStatus() {
		return publicationStatus;
	}
	public void setPublicationStatus(String publicationStatus) {
		if ( publicationStatus != null && publicationStatus.length() > 31 ) publicationStatus = publicationStatus.substring(0, 31);
		this.publicationStatus = publicationStatus;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		if ( author != null && author.length() > 63 ) author = author.substring(0, 63);
		this.author = author;
	}
	public String getParticipants() {
		return participants;
	}
	public void setParticipants(String participants) {
		if ( participants != null && participants.length() > 255 ) participants = participants.substring(0, 255);
		this.participants = participants;
	}
	public String getCaseCitation() {
		return caseCitation;
	}
	public void setCaseCitation(String caseCitation) {
		if ( caseCitation != null && caseCitation.length() > 255 ) caseCitation = caseCitation.substring(0, 255);
		this.caseCitation = caseCitation;
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
	public Set<PartyAttorneyPair> getPartyAttorneyPairs() {
		return partyAttorneyPairs;
	}
	public void setPartyAttorneyPairs(Set<PartyAttorneyPair> partyAttorneyPairs) {
		this.partyAttorneyPairs = partyAttorneyPairs;
	}
	
	
	
	@Override
	public String toString() {
        return getFileName();
    }
	@Override
	public int hashCode() {
		return slipOpinion.id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if ( !(obj instanceof SlipProperties) ) 
			return false;
		SlipProperties other = (SlipProperties) obj;
		return slipOpinion.id.equals( other.slipOpinion.id);
	}
}
