package opca.model;

import java.util.Date;
import java.util.regex.Pattern;

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(name="SlipOpinion.findAll", 
		query="select s from SlipOpinion s"),
	@NamedQuery(name="SlipOpinion.loadOpinionsWithJoins", 
		query="select distinct o from SlipOpinion o"),
	@NamedQuery(name="SlipOpinion.loadOpinionsWithJoinsForKeys", 
		query="select distinct o from SlipOpinion o where o.opinionKey in :opinionKeys"),
})
@NamedEntityGraphs({ 
	@NamedEntityGraph(name="fetchGraphForOpinionsWithJoins", attributeNodes= {
		@NamedAttributeNode(value="statuteCitations", subgraph="fetchGraphForOpinionsWithJoinsPartB")
	}, 
	subgraphs= {
		@NamedSubgraph(
			name = "fetchGraphForOpinionsWithJoinsPartB", 
			attributeNodes = { @NamedAttributeNode(value = "statuteCitation") } 
		),
	}) 
})
@SuppressWarnings("serial")
@Entity
public class SlipOpinion extends OpinionBase {
	private static Pattern fileNameSplit = Pattern.compile("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)");
	private final static int ONEMMM = 10000000;

	@Transient
	private SlipProperties slipProperties;
	@Transient
	private Summary summary;
	@Transient
	private Disposition disposition;
	@Transient
	private PartiesAndAttornies partiesAndAttornies;
	@Transient
	private TrialCourt trialCourt;
	@Transient
	private String searchUrl;

	public SlipOpinion() {
    	super();
    }
	public SlipOpinion(SlipOpinion slipOpinion) {
		super(slipOpinion);
		this.slipProperties = new SlipProperties(this, slipOpinion);
    }
	public SlipOpinion(String fileName, String fileExtension, String title, Date opinionDate, String court, String searchUrl) {
		super(null, title, opinionDate, court);
		setOpinionKey(new OpinionKey("1 Slip.Op " + generateOpinionKey(fileName)));
		slipProperties = new SlipProperties(this, fileName, fileExtension, court);
		this.searchUrl = searchUrl;
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
			if ( split.length >= 4  ) {
				return (ONEMMM*9) + (2*Integer.parseInt(split[1]+split[3])) + (split.length > 4?1:0);
			} else {
				StringBuilder sb = new StringBuilder();
				for ( char c: fileName.toCharArray()) {
					if ( Character.isDigit(c)) {
						sb.append(c);
					}
				}
				Integer v = 2*Integer.parseInt(sb.toString());
				if ( !Character.isDigit(fileName.charAt(fileName.length()-1))) {
					v = v + 1;
				}
				return (ONEMMM*9) + v;
			}
		case 'S':
			return (ONEMMM*10) + (2*Integer.parseInt(split[1])) + (split.length > 2?1:0); 
		default:
			// ouch
			Date d = new Date();
			return (int)d.getTime();
		}
	}
	public String getFileName() {
		return slipProperties.getFileName();
	}
	public void setFileName(String fileName) {
    	slipProperties.setFileName(fileName);
	}
	public String getFileExtension() {
		return slipProperties.getFileExtension();
	}
	public void setFileExtension(String fileExtension) {
    	slipProperties.setFileExtension(fileExtension);
	}
	public String getCourt() {
		return slipProperties.getCourt();
	}
	public void setCourt(String court) {
    	slipProperties.setCourt(court);
	}
	public Summary getSummary() {
		return summary;
	}
	public void setSummary(Summary summary) {
		this.summary = summary;
	}
	public Disposition getDisposition() {
		return disposition;
	}
	public void setDisposition(Disposition disposition) {
		this.disposition = disposition;
	}
	public PartiesAndAttornies getPartiesAndAttornies() {
		return partiesAndAttornies;
	}
	public void setPartiesAndAttornies(PartiesAndAttornies partiesAndAttornies) {
		this.partiesAndAttornies = partiesAndAttornies;
	}
	public TrialCourt getTrialCourt() {
		return trialCourt;
	}
	public void setTrialCourt(TrialCourt trialCourt) {
		this.trialCourt = trialCourt;
	}
	@Override
	public String toString() {
		if ( slipProperties != null )
			return String.format("%1$S : %2$tm/%2$td/%2$ty : %3$S", getFileName(), getOpinionDate(), getTitle() );
		else 
			return super.toString();
    }
	public SlipProperties getSlipProperties() {
		return slipProperties;
	}
	public void setSlipProperties(SlipProperties slipProperties) {
		this.slipProperties = slipProperties;
	}
    public String getSearchUrl() {
		return searchUrl;
	}
}
