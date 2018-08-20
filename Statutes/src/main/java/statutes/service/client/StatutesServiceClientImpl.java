package statutes.service.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import statutes.service.StatutesService;
import statutes.service.dto.KeyHierarchyPairs;
import statutes.service.dto.StatuteHierarchy;
import statutes.service.dto.StatuteKeyArray;
import statutes.service.dto.StatutesRootArray;
import statutes.service.dto.StatutesTitlesArray;

public class StatutesServiceClientImpl implements StatutesServiceClient {
	private WebTarget statutes;
	private WebTarget statutesTitles;
	private WebTarget statuteHierarchy;
	private WebTarget findStatutes;

	public StatutesServiceClientImpl(URL apiLocation) {
		try {
			javax.ws.rs.client.Client client = ClientBuilder.newClient();
			statutes = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + StatutesService.STATUTES, 
					null, null)
				);

			statutesTitles = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + StatutesService.STATUTESTITLES, 
					null, null)
				);

			statuteHierarchy = client
					.target(new URI(
							apiLocation.getProtocol(), 
							apiLocation.getUserInfo(), 
							apiLocation.getHost(), 
							apiLocation.getPort(), 
							apiLocation.getPath() + StatutesService.STATUTEHIERARCHY, 
							null, null)
						);
			findStatutes = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + StatutesService.STATUTESANDHIERARCHIES, 
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
