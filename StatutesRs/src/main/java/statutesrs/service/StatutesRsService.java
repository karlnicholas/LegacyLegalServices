package statutesrs.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import service.Client;
import statutesca.factory.CAStatutesFactory;
import statutesrs.ReferencesWithReferences;
import statutesrs.ResponseArray;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

@Path("statutes")
@Produces("application/json")
public class StatutesRsService implements Client {

	@Override
	@GET
	public StatutesRootArray getStatutes() {
		StatutesRootArray statutesRootArray = new StatutesRootArray();
		statutesRootArray.getItem().addAll(CAStatutesFactory.getInstance().getParserInterface(true).getStatutes());
		return statutesRootArray;
	}

	@Override
	public StatutesTitlesArray getStatutesTitles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReferencesWithReferences returnReferencesByTitle(String fullFacet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseArray findStatutes(StatuteKeyArray statuteKeyArray) {
		// TODO Auto-generated method stub
		return null;
	}
}
