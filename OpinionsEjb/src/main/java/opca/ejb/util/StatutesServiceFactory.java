package opca.ejb.util;

import java.net.MalformedURLException;
import java.net.URL;

import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

public class StatutesServiceFactory {
	private static URL rsLocation;
	private static final String defaultAddress = "http://localhost:8080/statutesrs/rs/";
	private static StatutesServiceFactory instance = new StatutesServiceFactory();
	private StatutesServiceFactory() {
		try {
			String s = System.getenv("statutesrsservice");
			if (s != null)
				rsLocation = new URL(s);
			else
				rsLocation = new URL(defaultAddress);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	public static StatutesServiceFactory getInstance() {
		return instance;
	}
	public StatutesService getStatutesServiceClient() {
			return new StatutesServiceClientImpl(rsLocation);
	}
}
