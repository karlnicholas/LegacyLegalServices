package opca.model;

import java.io.Serializable;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class SlipProperties implements Serializable {
	@Id
	private OpinionKey opinionKey;
    @OneToOne @MapsId
	private SlipOpinion slipOpinion; 
	@Column(columnDefinition="varchar(15)")
	private String court;
	@Column(columnDefinition = "varchar(31)")
    private String fileName;
	@Column(columnDefinition = "varchar(7)")
	private String fileExtension;
	@Column(columnDefinition = "varchar(31)")
    private String disposition;
	@Column(columnDefinition = "varchar(4008)")
    private String summary;

    public SlipProperties() {}
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
		if ( summary != null && summary.length() > 4008 ) summary = "..." + summary.substring(summary.length()-4005);
		this.summary = summary;
	}
	@Override
	public String toString() {
        return getFileName();
    }
}
