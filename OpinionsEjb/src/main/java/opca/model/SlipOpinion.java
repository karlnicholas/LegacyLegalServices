package opca.model;

import java.util.Date;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;

@NamedQueries({
	@NamedQuery(name="SlipOpinion.findByOpinionDate", 
		query="select o from SlipOpinion o where o.opinionDate=:opinionDate"),
	@NamedQuery(name="SlipOpinion.findByOpinionDateRange", 
		query="select o from SlipOpinion o where o.opinionDate between :startDate and :endDate order by o.opinionDate desc"),
	@NamedQuery(name="SlipOpinion.loadByOpinionDateRange", 
		query="select distinct o from SlipOpinion o left join fetch o.statuteCitations left join fetch o.opinionCitations where o.opinionDate between :startDate and :endDate order by o.opinionDate desc"),
	@NamedQuery(name="SlipOpinion.listOpinionDates", 
		query="select distinct o.opinionDate from SlipOpinion o order by o.opinionDate desc"),
	@NamedQuery(name="SlipOpinion.findByOpinionKey", 
		query="select o from SlipOpinion o where o.opinionKey = :key"),
	@NamedQuery(name="SlipOpinion.fetchStatuteCitations", 
		query="select elements(so.statuteCitations) from SlipOpinion as so where so.opinionKey = :key"),
	@NamedQuery(name="SlipOpinion.fetchOpinionCitations", 
		query="select elements(so.opinionCitations) from SlipOpinion as so where so.opinionKey = :key"),
	@NamedQuery(name="SlipOpinion.fetchReferringOpinions", 
		query="select elements(so.referringOpinions) from SlipOpinion as so where so.opinionKey = :key"),
})
@SuppressWarnings("serial")
@Entity
@Table(indexes = {@Index(columnList="volume,vset,page")}) 
public class SlipOpinion extends OpinionBase {
	private static Pattern fileNameSplit = Pattern.compile("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)");
//	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
	@Column(columnDefinition = "TEXT")
    private String fileName;
	@Column(columnDefinition = "TEXT")
	private String fileExtension;
	@Column(columnDefinition = "TEXT")
    private String disposition;
	@Column(columnDefinition = "TEXT")
    private String summary;

	final static int ONEMMM = 10000000;
    public SlipOpinion() {
    	super();
    }
	public SlipOpinion(SlipOpinion slipOpinion) {
		super(slipOpinion);
//		this.id = opinionSummary.id;
    	this.disposition = slipOpinion.disposition;
    	this.summary = slipOpinion.summary;
    	this.fileName = slipOpinion.fileName;
    	this.fileExtension = slipOpinion.fileExtension;
    }
	public SlipOpinion(String fileName, String fileExtension, String title, Date opinionDate, String court) {
		super(null, title, opinionDate, court);
		setOpinionKey(new OpinionKey("1 Slip.Op " + generateOpinionKey(fileName)));
		this.fileName = fileName;
		this.fileExtension = fileExtension;
    	this.disposition = null;
    	this.summary = null;
    }

//	public Long getId() {
//		return id;
//	}
	
	private int generateOpinionKey(String fileName) {
		String[] split = fileNameSplit.split(fileName);
		if ( split.length < 2 ) throw new RuntimeException("Filename funny!" + fileName);
		switch(split[0].charAt(0)) {
		case 'A':
			return ONEMMM + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'B':
			return (ONEMMM*2) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'C':
			return (ONEMMM*3) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'D':
			return (ONEMMM*4) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'E':
			return (ONEMMM*5) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'F':
			return (ONEMMM*6) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'G':
			return (ONEMMM*7) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'H':
			return (ONEMMM*8) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		case 'J':
			return (ONEMMM*9) + (2*Integer.parseInt(split[1]+split[3])) + (split.length > 4?1:0); 
		case 'S':
			return (ONEMMM*10) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		default:
			// ouch
			Date d = new Date();
			return (int)d.getTime();
		}
	}
	public String getFileName() {
		return fileName;
	}
    @XmlElement
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileExtension() {
		return fileExtension;
	}
    @XmlElement
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public String getDisposition() {
		return disposition;
	}
    @XmlElement
	public void setDisposition(String disposition) {
		if ( disposition != null && disposition.length() > 250 ) disposition = disposition.substring(0, 250);
		this.disposition = disposition;
	}
	public String getSummary() {
		return summary;
	}
    @XmlElement
	public void setSummary(String summary) {
		if ( summary != null && summary.length() > 4090 ) summary = "..." + summary.substring(summary.length()-4087);
		this.summary = summary;
	}
	@Override
	public String toString() {
        return String.format("%1$S : %2$tm/%2$td/%2$ty : %3$S", getFileName(), getOpinionDate(), getTitle() );
    }
}
