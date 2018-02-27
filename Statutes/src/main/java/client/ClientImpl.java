package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import service.Client;
import statutesrs.ReferencesWithReferences;
import statutesrs.ResponseArray;
import statutesrs.StatuteKeyArray;
import statutesrs.StatutesRootArray;
import statutesrs.StatutesTitlesArray;

public class ClientImpl implements Client {
	public static final String STATUTES = "statutes";
	public static final String STATUTESTITLES = "statutestitles";
	public static final String REFERENCEBYTITLE = "referencebytitle";
	public static final String FINDSTATUTES = "findstatutes";
	private javax.ws.rs.client.Client client;
	private URI uriStatutes;
	private URI uriStatutesTitles;
	private URI uriReferencesByTitle;
	private URI uriFindStatutes;

	protected ClientImpl(URL apiLocation) {
		try {
			uriStatutes = new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + STATUTES, 
					null, null);
			uriStatutesTitles = new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + STATUTESTITLES, 
					null, null);
			uriReferencesByTitle = new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + REFERENCEBYTITLE, 
					null, null);
			uriFindStatutes = new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + FINDSTATUTES, 
					null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		client = ClientBuilder.newClient();
	}
	
	@Override
	public StatutesRootArray getStatutes() {
		return client
			.target(uriStatutes)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(StatutesRootArray.class);
	}

	@Override
	public StatutesTitlesArray getStatutesTitles() {
		return client
			.target(uriStatutesTitles)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(StatutesTitlesArray.class);
	}

	@Override
	public ReferencesWithReferences returnReferencesByTitle(String fullFacet) {
		return client
			.target(uriReferencesByTitle)
			.queryParam("fullFacet", fullFacet)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(ReferencesWithReferences.class);
	}

	@Override
	public ResponseArray findStatutes(StatuteKeyArray statuteKeyArray) {
		return client
				.target(uriFindStatutes)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(statuteKeyArray, MediaType.APPLICATION_JSON_TYPE), ResponseArray.class);
	}

}
