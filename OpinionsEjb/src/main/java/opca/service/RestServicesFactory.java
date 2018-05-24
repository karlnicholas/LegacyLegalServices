package opca.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import service.Client;
import client.StatutesRsService;

/**
 * This class is a singleton that loads and holds all Role definitions from the
 * database. Very specific to this particular application. Should really only be
 * used by UserAccountEJB.
 * 
 * @author Karl Nicholas
 *
 */
public class RestServicesFactory {
	private Logger logger = Logger.getLogger(RestServicesFactory.class.getName());
	private final String defaultAddress = "http://localhost:8080/statutesrs/rs/";

	// private constructor to avoid client applications to use constructor
	public Client connectStatutesRsService() {
		try {
			String s = System.getenv("statutesrsservice");
			Client statutesRs = null;
			URL rsLocation;
			if (s != null)
				rsLocation = new URL(s);
			else
				rsLocation = new URL(defaultAddress);
			int retryCount = 3;
			while (retryCount-- > 0) {
				try {
					statutesRs = new StatutesRsService(rsLocation).getRsService();
				} catch (Exception e) {
					logger.info("new StatutesWSService - Exception");
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e2) {
						throw new RuntimeException(e2);
					}
				}
				retryCount = 0;
			}
			return statutesRs;
		} catch (MalformedURLException e1) {
			throw new RuntimeException(e1);
		}
	}
}
