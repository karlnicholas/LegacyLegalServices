package statutesca.factory;

import java.util.logging.Logger;

import parser.ParserInterface;
import statutesca.code.CALoadStatutes;

public class CAStatutesFactory {
	Logger logger = Logger.getLogger(CAStatutesFactory.class.getName());

	private static ParserInterface parserInterface = null;
    private CAStatutesFactory(){}
    private static class SingletonHelper {
        private static final CAStatutesFactory INSTANCE = new CAStatutesFactory();
    }
    public static CAStatutesFactory getInstance(){
        return SingletonHelper.INSTANCE;
    }

	public synchronized ParserInterface getParserInterface(boolean loadXMLCodes) {
		if ( parserInterface == null ) {
			parserInterface = new CALoadStatutes();
			if ( loadXMLCodes ) {
				parserInterface.loadStatutes();
			}
		}
		return parserInterface;
	}

}
