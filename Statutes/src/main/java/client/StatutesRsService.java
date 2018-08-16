package client;

import java.net.URL;

import service.StatutesService;

public class StatutesRsService  {
	private ClientImpl clientImpl;
	
	public StatutesRsService(URL apiLocation) {
		clientImpl = new ClientImpl(apiLocation);
	}

	public StatutesService getRsService() {
		return clientImpl;
	}

}
