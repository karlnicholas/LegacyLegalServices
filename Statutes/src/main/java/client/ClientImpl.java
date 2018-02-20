package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import service.Client;
import statutes.StatutesRoot;
import statutesrs.ReferencesWithReferences;
import statutesrs.ResponseArray;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

public class ClientImpl implements Client {
	private static final String STATUTES = "statutes";
	private static final String STATUTESTITLES = "statutestitles";
	private static final String REFERENCEBYTITLE = "referencebytitle";
	private static final String FINDSTATUTES = "findstatutes";
	private javax.ws.rs.client.Client client;
	private URI uriStatutes;
	private URI uriStatutesTitles;
	private URI uriReferencesByTitle;
	private URI uriFindStatutes;

	protected ClientImpl(URL apiLocation) {
		try {
			uriStatutes = new URI(apiLocation.getProtocol(), apiLocation.getUserInfo(), apiLocation.getHost(), apiLocation.getPort(), apiLocation.getPath() + STATUTES, null, null);
			uriStatutesTitles = new URI(apiLocation.getProtocol(), apiLocation.getHost(), apiLocation.getPath() + STATUTESTITLES, null);
			uriReferencesByTitle = new URI(apiLocation.getProtocol(), apiLocation.getHost(), apiLocation.getPath() + REFERENCEBYTITLE, null);
			uriFindStatutes = new URI(apiLocation.getProtocol(), apiLocation.getHost(), apiLocation.getPath() + FINDSTATUTES, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		client = ClientBuilder.newClient();
	}
	
	@Override
	public StatutesRootArray getStatutes() {
		return client.target(uriStatutes).request(MediaType.APPLICATION_JSON_TYPE).get(StatutesRootArray.class);
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
