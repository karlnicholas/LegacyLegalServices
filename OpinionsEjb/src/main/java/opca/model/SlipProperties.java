package opca.model;

import java.io.Serializable;
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
	@Column(columnDefinition = "varchar(31)")
    private String disposition;
	@Column(columnDefinition = "varchar(4007)")
    private String summary;

	public SlipProperties() {}
	public SlipProperties(SlipOpinion slipOpinion) {
		this.slipOpinion = slipOpinion;
	}
	public SlipProperties(SlipOpinion slipOpinion, String fileName, String fileExtension, String court, String disposition, String summary) {
		this.slipOpinion = slipOpinion;
    	setFileName(fileName);
    	setFileExtension(fileExtension);
		setCourt(court);
    	setDisposition(disposition);
    	setSummary(summary);
    }

	public SlipProperties(SlipOpinion slipOpinion, SlipOpinion slipCopy) {
		this.slipOpinion = slipOpinion;
    	setFileName(slipCopy.getFileName());
    	setFileExtension(slipCopy.getFileExtension());
		setCourt(slipCopy.getCourt());
    	setDisposition(slipCopy.getDisposition());
    	setSummary(slipCopy.getSummary());
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
		if ( court != null && court.length() > 31 ) court = court.substring(0, 31);
		this.court = court;
	}
	public String getDisposition() {
		return disposition;
	}
	public void setDisposition(String disposition) {
		if ( disposition != null && disposition.length() > 31 ) disposition = disposition.substring(0, 31);
		this.disposition = disposition;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		if ( summary != null && summary.length() > 4007 ) summary = "..." + summary.substring(summary.length()-4004);
		this.summary = summary;
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
