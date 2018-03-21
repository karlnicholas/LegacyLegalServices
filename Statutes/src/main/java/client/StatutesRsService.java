package client;

import java.net.URL;

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
