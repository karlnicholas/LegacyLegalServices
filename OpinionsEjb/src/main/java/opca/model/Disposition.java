package opca.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class Disposition {
	@Id
	private Integer opinionId;
    @OneToOne(fetch=FetchType.LAZY) @MapsId
	private SlipOpinion slipOpinion; 

    @Column(columnDefinition="varchar(127)")
	private String description; 
	private Date date; 
    @Column(columnDefinition="varchar(255)")
	private String dispositionType; 
    @Column(columnDefinition="varchar(31)")
	private String publicationStatus; 
    @Column(columnDefinition="varchar(63)")
	private String author; 
    @Column(columnDefinition="varchar(255)")
	private String participants; 
    @Column(columnDefinition="varchar(255)")
	private String caseCitation;
    public Disposition() {}
	public Disposition(SlipOpinion slipOpinion, String description, Date date,
			String dispositionType, String publicationStatus, String author, String participants, String caseCitation) {
		this.slipOpinion = slipOpinion;
		this.description = description;
		this.date = date;
		this.dispositionType = dispositionType;
		this.publicationStatus = publicationStatus;
		this.author = author;
		this.participants = participants;
		this.caseCitation = caseCitation;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		if ( description != null && description.length() > 127 ) description = description.substring(0, 127);
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDispositionType() {
		return dispositionType;
	}
	public void setDispositionType(String dispositionType) {
		if ( description != null && description.length() > 255 ) description = description.substring(0, 255);
		this.dispositionType = dispositionType;
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
}
