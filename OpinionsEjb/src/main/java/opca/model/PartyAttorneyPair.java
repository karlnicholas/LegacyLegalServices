package opca.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PartyAttorneyPair {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	private SlipProperties slipProperties;	
    @Column(columnDefinition="varchar(1023)")
	private String party;
    @Column(columnDefinition="varchar(3071)")
	private String attorney;
    
	public PartyAttorneyPair() {}

	public PartyAttorneyPair(SlipProperties slipProperties, String party, String attorney) {
		this.slipProperties = slipProperties;
		this.party = party;
		this.attorney = attorney;
	}
	public Integer getId() {
		return id;
	}
	public SlipProperties getSlipProperties() {
		return slipProperties;
	}
	public void setSlipProperties(SlipProperties slipProperties) {
		this.slipProperties = slipProperties;
	}
	public String getParty() {
		return party;
	}
	public void setParty(String party) {
		if ( party != null && party.length() > 1023 ) party = party.substring(0, 1023);
		this.party = party;
	}
	public String getAttorney() {
		return attorney;
	}
	public void setAttorney(String attorney) {
		if ( attorney != null && attorney.length() > 3071 ) attorney = attorney.substring(0, 3071);
		this.attorney = attorney;
	}	
}
