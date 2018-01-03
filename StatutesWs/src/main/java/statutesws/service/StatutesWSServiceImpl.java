package statutesws.service;

import javax.jws.WebService;

import statutesca.factory.CAStatutesFactory;
import statutesws.server.StatutesWSImpl;

@WebService(serviceName = "StatutesWS", endpointInterface = "service.StatutesWS", 
targetNamespace = "http://statutesws/",  portName="StatutesWSPort")
public class StatutesWSServiceImpl extends StatutesWSImpl {

	public StatutesWSServiceImpl() {
		super( CAStatutesFactory.getInstance().getParserInterface(true));
	}
}
