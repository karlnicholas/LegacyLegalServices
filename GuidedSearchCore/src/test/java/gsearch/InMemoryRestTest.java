package gsearch;

public class InMemoryRestTest {
	/*
	private static UndertowJaxrsServer server;
	@ApplicationPath("/rs")
	public static class MyApp extends Application {
		@Override
		public Set<Class<?>> getClasses() {
			HashSet<Class<?>> classes = new HashSet<Class<?>>();
			classes.add(StatutesServiceServer.class);
			return classes;
		}
	}

	@BeforeClass
	public static void init() throws Exception {
		server = new UndertowJaxrsServer().start();
	}

	@AfterClass
	public static void stop() throws Exception {
		server.stop();
	}

	@Test
	public void testApplicationPath() throws Exception {
		server.deploy(MyApp.class);
		String url = TestPortProvider.generateURL("/rs/");
		StatutesServiceClientImpl statutesService = new StatutesServiceClientImpl(new URL(url));
		StatutesTitlesArray titles = statutesService.getStatutesTitles();
	}
*/
}