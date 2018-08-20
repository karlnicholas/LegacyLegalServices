package statutes.service.server;

import java.util.ServiceLoader;

import statutes.api.IStatutesApi;

public class ApiImplSingleton {
	private IStatutesApi iStatutesApi;
	private static final ApiImplSingleton INSTANCE = new ApiImplSingleton();
	private ApiImplSingleton() {
		ServiceLoader<IStatutesApi> parserLoader = 
				ServiceLoader.load(IStatutesApi.class);
		iStatutesApi = parserLoader.iterator().next();
		if ( iStatutesApi == null ) {
			throw new RuntimeException("ParserInterface not found.");
		}
		iStatutesApi.loadStatutes();
	}

	public static ApiImplSingleton getInstance() {
		return INSTANCE;
	}
	
	public IStatutesApi getStatutesApi() {
		return iStatutesApi;
	}
}
