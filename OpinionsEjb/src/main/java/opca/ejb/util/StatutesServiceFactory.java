package opca.ejb.util;

import java.net.MalformedURLException;
import java.net.URL;

import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

public class StatutesServiceFactory {
	private static final URL rsLocation;
	private static final String defaultAddress = "http://localhost:8080/statutesrs/rs/";
	static {
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
	public static StatutesService getStatutesServiceClient() {
			return new StatutesServiceClientImpl(rsLocation);
	}
}
