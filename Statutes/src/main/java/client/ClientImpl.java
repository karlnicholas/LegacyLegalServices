package client;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import service.StatutesService;
import statutesrs.*;

public class ClientImpl implements StatutesService {
	public static final String STATUTES = "statutes";
	public static final String STATUTESTITLES = "statutestitles";
	public static final String REFERENCEBYTITLE = "referencebytitle";
	public static final String FINDSTATUTES = "findstatutes";

	private WebTarget statutes;
	private WebTarget statutesTitles;
	private WebTarget statuteHierarchy;
	private WebTarget findStatutes;

	protected ClientImpl(URL apiLocation) {
		try {
			javax.ws.rs.client.Client client = ClientBuilder.newClient();
			statutes = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + STATUTES, 
					null, null)
				);

			statutesTitles = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + STATUTESTITLES, 
					null, null)
				);

			statuteHierarchy = client
					.target(new URI(
							apiLocation.getProtocol(), 
							apiLocation.getUserInfo(), 
							apiLocation.getHost(), 
							apiLocation.getPort(), 
							apiLocation.getPath() + REFERENCEBYTITLE, 
							null, null)
						);
			findStatutes = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + FINDSTATUTES, 
					null, null)
				);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public StatutesRootArray getStatutesRoots() {
		return statutes
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(StatutesRootArray.class);
	}

	@Override
	public StatutesTitlesArray getStatutesTitles() {
		return statutesTitles
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(StatutesTitlesArray.class);
	}

	@Override
	public StatuteHierarchy getStatuteHierarchy(String fullFacet) {
		return statuteHierarchy
			.queryParam("fullFacet", fullFacet)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get(StatuteHierarchy.class);
	}

	@Override
	public KeyHierarchyPairs getStatutesAndHierarchies(StatuteKeyArray statuteKeyArray) {
		return findStatutes
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(statuteKeyArray, MediaType.APPLICATION_JSON_TYPE), KeyHierarchyPairs.class);
	}

}
