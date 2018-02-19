package opca.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import service.StatutesRs;
import client.StatutesRsService;
/**
 * This class is a singleton that loads and holds all Role definitions from 
 * the database. Very specific to this particular 
 * application. Should really only be used by UserAccountEJB.
 * 
 * @author Karl Nicholas
 *
 */
public class RestServicesService {
	private Logger logger = Logger.getLogger(RestServicesService.class.getName());
	private String statutesRsServiceUrl = "http://statutesrs-jsec.rhcloud.com:80/StatutesRs";
	protected Properties applicationProps;

	public RestServicesService() {
		Properties defaultProps = new Properties();
		// create and load default properties
		try ( InputStream in = this.getClass().getResourceAsStream("/defaultProperties.properties") ) {
			if ( in != null ) defaultProps.load(in);
			if ( in != null ) in.close();
		} catch (FileNotFoundException e) {
			// ignored
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		try ( InputStream in = this.getClass().getResourceAsStream("/appProperties.properties") ) {			
			// create application properties with default
			applicationProps = new Properties(defaultProps);
			// now load properties 
			// from last invocation
			if ( in != null ) applicationProps.load(in);
			else applicationProps = defaultProps;
			if ( in != null ) in.close();
		} catch (FileNotFoundException e) {
			// ignored
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
    //private constructor to avoid client applications to use constructor
    public StatutesRs connectStatutesRsService() {
    	String serviceUrl = applicationProps.getProperty("statutesWSServiceUrl");
    	if ( serviceUrl == null ) serviceUrl = statutesRsServiceUrl;
	    StatutesRs statutesRs = null;
	    int retryCount = 3;
	    while ( retryCount-- > 0 ) {
	        try {
	//        	statutesWS = new StatutesWSService(new URL("http://localhost:9080/StatutesWS?wsdl")).getStatutesWSPort();
	        	statutesRs = new StatutesRsService(new URL(serviceUrl)).getStatutesRsPort();
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
    }
}

