package opca.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import service.StatutesWS;
import client.StatutesWSService;
/**
 * This class is a singleton that loads and holds all Role definitions from 
 * the database. Very specific to this particular 
 * application. Should really only be used by UserAccountEJB.
 * 
 * @author Karl Nicholas
 *
 */
public class WebServicesService {
	private Logger logger = Logger.getLogger(WebServicesService.class.getName());
	private String statutesWSServiceUrl = "http://statutesws-jsec.rhcloud.com:80/StatutesWS?wsdl";
	protected Properties applicationProps;

	public WebServicesService() {
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
    public StatutesWS connectStatutesWSService() {
    	String serviceUrl = applicationProps.getProperty("statutesWSServiceUrl");
    	if ( serviceUrl == null ) serviceUrl = statutesWSServiceUrl;
	    StatutesWS statutesWS = null;
	    int retryCount = 3;
	    while ( retryCount-- > 0 ) {
	        try {
	//        	statutesWS = new StatutesWSService(new URL("http://localhost:9080/StatutesWS?wsdl")).getStatutesWSPort();
	        	statutesWS = new StatutesWSService(new URL(serviceUrl)).getStatutesWSPort();
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
	    return statutesWS;
    }
}

