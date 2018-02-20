package client;

import java.net.URI;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import service.Client;

public class StatutesRsService  {
	private URL apiLocation;
	
	public StatutesRsService(URL apiLocation) {
		this.apiLocation = apiLocation;
	}

	public Client getRsService() {
		return new ClientImpl(apiLocation);
	}

}
