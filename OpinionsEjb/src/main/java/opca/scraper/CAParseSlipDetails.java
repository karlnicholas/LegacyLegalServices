package opca.scraper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class CAParseSlipDetails {
	private static final Logger logger = Logger.getLogger(CAParseSlipDetails.class.getName());
	private static final String baseUrl = "http://appellatecases.courtinfo.ca.gov"; 
	private static final String searchUrl 
		= "/search/searchResults.cfm?dist=0&search=number&useSession=0&query_caseNumber=";
	private final boolean debugFiles; 
	
	public CAParseSlipDetails(boolean debugFiles) {
		this.debugFiles = debugFiles;
		logger.info("CAParseSlipDetails");
	}
	
	public static void main(String[] args) {
		new CAParseSlipDetails(true).run("S099549");
	}

	static class MyRedirectStrategy extends LaxRedirectStrategy {
		private String location;
		
		@Override
		public boolean isRedirected(HttpRequest arg0, HttpResponse arg1, HttpContext arg2) throws ProtocolException {
			logger.info("HttpRequest arg0: " + arg0 + " HttpResponse arg1: " + arg1 + " HttpContext arg2: " + arg2);
			return super.isRedirected(arg0, arg1, arg2);
		}
		
		@Override
		public HttpUriRequest getRedirect(HttpRequest arg0, HttpResponse arg1, HttpContext arg2) throws ProtocolException {
			logger.info("HttpRequest arg0: " + arg0 + " HttpResponse arg1: " + arg1 + " HttpContext arg2: " + arg2);
			location = arg1.getHeaders("Location")[0].getValue();
			return super.getRedirect(arg0, arg1, arg2);
		}
	}
	
	private void run(String slipOpinionName) {
		
		MyRedirectStrategy myRedirectStrategy = new MyRedirectStrategy();
		try ( CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(myRedirectStrategy).build() ) {
			HttpGet httpGet = new HttpGet(baseUrl+searchUrl+slipOpinionName);
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
	        		saveCopyOfCaseDetail(CACaseScraper.casesDir, slipOpinionName+"-mainCaseScreen.html", new BufferedInputStream(bais));
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
				parseOpinionDetail(is); 
				response.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
			httpGet = new HttpGet(baseUrl+myRedirectStrategy.location.replace("mainCaseScreen", "disposition"));
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
	        		saveCopyOfCaseDetail(CACaseScraper.casesDir, slipOpinionName+"-disposition.html", new BufferedInputStream(bais));
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
				parseOpinionDetail(is); 
				response.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
				// retry three times
			}
			httpGet = new HttpGet(baseUrl+myRedirectStrategy.location.replace("mainCaseScreen", "partiesAndAttorneys"));
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
	        		saveCopyOfCaseDetail(CACaseScraper.casesDir, slipOpinionName+"-partiesAndAttorneys.html", new BufferedInputStream(bais));
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
				parseOpinionDetail(is); 
				response.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
			httpGet = new HttpGet(baseUrl+myRedirectStrategy.location.replace("mainCaseScreen", "trialCourt"));
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
	        		saveCopyOfCaseDetail(CACaseScraper.casesDir, slipOpinionName+"-trialCourt.html", new BufferedInputStream(bais));
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
				parseOpinionDetail(is); 
				response.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
			// we are going to shut down the connection manager before leaving
			httpClient.close();
		} catch (IOException ex) {
	    	logger.log(Level.SEVERE, null, ex);
		}
		
	}

	protected void parseOpinionDetail(InputStream inputStream) {
		
		try ( BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8" )) ) {
		
			List<String> lines = new ArrayList<String>();
			String tmpString;

			while ((tmpString = reader.readLine()) != null) {
				lines.add(tmpString);
			}
			reader.close();
			Iterator<String> si = lines.iterator();
	
			DateFormat dfs = DateFormat.getDateInstance(DateFormat.SHORT);
			
			while (si.hasNext()) {
				String line = si.next();
				System.out.println(line);
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
	}

	private void saveCopyOfCaseDetail(String directory, String fileName, InputStream inputStream ) {		
	    try ( OutputStream out = Files.newOutputStream(Paths.get(directory + "/" + fileName)) ) {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
	    	int b;
	    	while ( (b = inputStream.read()) != -1 ) {
	    		out.write(b);
	    		baos.write(b);
	    	}
	    	out.close();
	    	baos.close();
	    	inputStream.close();
	    } catch( IOException ex) {
	    	logger.log(Level.SEVERE, null, ex);
	    }
	}


}
