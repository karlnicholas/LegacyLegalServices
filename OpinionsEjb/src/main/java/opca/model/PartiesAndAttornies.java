package opca.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class PartiesAndAttornies {
	@Id
	private Integer opinionId;
    @OneToOne(fetch=FetchType.LAZY) @MapsId
	private SlipOpinion slipOpinion; 

    @OneToMany(mappedBy="partiesAndAttornies")
    private Set<PartyAttorneyPair> partyAttorneyPairs;
}
