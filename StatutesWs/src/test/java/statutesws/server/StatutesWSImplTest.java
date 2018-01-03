package statutesws.server;

import statutesws.StatutesRootArray;
import statutesws.server.StatutesWSImpl;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import client.StatutesWSService;
import service.StatutesWS;
import statutesca.factory.CAStatutesFactory;

public class StatutesWSImplTest {
	private static Endpoint endpoint;
	private static final String address = "http://localhost:9000/statutesws";

	@WebService(serviceName = "StatutesWS", endpointInterface = "service.StatutesWS", 
			targetNamespace = "http://statutesws/",  portName="StatutesWSPort")
	private static class StatutesWSServiceImpl extends StatutesWSImpl {

		public StatutesWSServiceImpl() {
			super( CAStatutesFactory.getInstance().getParserInterface(true));
		}
	}

	@BeforeClass
	public static void setupTest() {
		StatutesWSServiceImpl implementor = new StatutesWSServiceImpl();
		endpoint = Endpoint.publish(address, implementor);
	}

	@AfterClass
	public static void shutdownTest() {
		endpoint.stop();
	}

	@Test
	public void testClient() {
		StatutesWSService service;
		URL wsdlLocation;
		try {
			wsdlLocation = new URL(address + "/StatutesWS?wsdl");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		service = new StatutesWSService(wsdlLocation);
		StatutesWS statutesWS = service.getStatutesWSPort();
		StatutesRootArray statutes = statutesWS.getStatutes();
		assertEquals( 29, statutes.getItem().size() );
	}

}
