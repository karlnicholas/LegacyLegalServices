package opca.scraper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.parser.ScrapedOpinionDocument;

public class CACaseScraper implements OpinionScraperInterface {
	private static final Logger logger = Logger.getLogger(CACaseScraper.class.getName());
	
	public final static String casesDir = "c:/users/karln/opca/opjpa/cases/";
	public final static String caseListFile = "c:/users/karln/opca/opjpa/html/60days.html";
	private final static String downloadURL = "http://www.courts.ca.gov/opinions/documents/";	
	private final boolean debugFiles; 
	
	public CACaseScraper(boolean debugFiles) {
		this.debugFiles = debugFiles;
		logger.info("CACaseScraper");
	}

	@Override
	public List<SlipOpinion> getCaseList() {
		List<SlipOpinion> slipOpinionList = null;
		try ( CloseableHttpClient httpclient = HttpClients.createDefault() ) {
			HttpGet httpGet = new HttpGet("http://www.courts.ca.gov/cms/opinions.htm?Courts=Y");
			CloseableHttpResponse response = httpclient.execute(httpGet);
				
			HttpEntity entity = response.getEntity();
			logger.info("HTTP Response: " + response.getStatusLine());
			// need to read into memory here because 
			// we are going to shut down the connection manager before leaving
			ByteArrayInputStream bais = convertInputStream(entity.getContent());
			response.close();
			if ( debugFiles ) {
				saveCopyOfCaseList(new BufferedReader( new InputStreamReader(bais, "UTF-8" )));
				bais.reset();
			}
			slipOpinionList = parseCaseList(bais);
			httpclient.close();
		} catch (IOException ex ) {
			logger.severe(ex.getMessage());
		}
		return slipOpinionList;
	}

	@Override
	public List<ScrapedOpinionDocument> scrapeOpinionFiles(List<SlipOpinion> slipOpinions) {
		List<ScrapedOpinionDocument> documents = new ArrayList<ScrapedOpinionDocument>();
		CAParseScrapedDocument parseScrapedDocument = new CAParseScrapedDocument();
		
		try ( CloseableHttpClient httpclient = HttpClients.createDefault() ) {
			for (SlipOpinion slipOpinion: slipOpinions ) {
				logger.fine("Downloading: " + slipOpinion.getFileName()+ slipOpinion.getFileExtension());
				HttpGet httpGet = new HttpGet(downloadURL + slipOpinion.getFileName() + slipOpinion.getFileExtension());
				try ( CloseableHttpResponse response = httpclient.execute(httpGet) ) {
					// uhmm .. I don't think the key is the same as the name.
//					HttpGet httpGet = new HttpGet("http://www.courts.ca.gov/opinions/documents/" + slipOpinion.getName() + slipOpinion.getFileExtension());
					InputStream is;
		        	if ( debugFiles ) {
						ByteArrayInputStream bais = convertInputStream(response.getEntity().getContent());
		        		saveCopyOfCase(casesDir, slipOpinion.getFileName().toString()+slipOpinion.getFileExtension(), new BufferedInputStream(bais));
		        		bais.reset();
		        		is = bais;
		        	} else {
		        		is = response.getEntity().getContent();
		        	}
					documents.add( parseScrapedDocument.parseScrapedDocument(slipOpinion, is) ); 
					response.close();
				} catch (IOException ex) {
					logger.log(Level.SEVERE, null, ex);
					logger.log(Level.SEVERE, "Problem with file " + slipOpinion.getFileName()+slipOpinion.getFileExtension());
					// retry three times
				}
			}
			// we are going to shut down the connection manager before leaving
			httpclient.close();
		} catch (IOException ex) {
	    	logger.log(Level.SEVERE, null, ex);
		}
		return documents;
	}
	
	private ByteArrayInputStream convertInputStream(InputStream inputStream) {
		ByteArrayInputStream bais = null;
        try ( ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ) {
	    	int b;
	    	while ( (b = inputStream.read()) != -1 ) {
	    		outputStream.write(b);
	    	}
	    	outputStream.close();
	    	bais = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
	    	logger.log(Level.SEVERE, null, ex);
		}
        return bais; 
	}
	
	private void saveCopyOfCase(String directory, String fileName, InputStream inputStream ) {		
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

	protected List<SlipOpinion> parseCaseList(InputStream inputStream) {
		ArrayList<SlipOpinion> cases = new ArrayList<SlipOpinion>();
		Date sopDate;
		Date opDate = null;
		
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
				// System.out.println(line);
				if (line.contains("/opinions/documents/")) {
					String fileExtension = ".DOCX"; 
					int loc = line.indexOf(fileExtension);
					if ( loc == -1) {
						fileExtension = ".DOC";
						loc = line.indexOf(fileExtension);
					}
					String fileName = line.substring(loc - 8, loc + 4);
					if (fileName.charAt(0) == '/') fileName = fileName.substring(1);
					loc = line.indexOf("<td valign=\"top\">");
					// String publishDate = line.substring(loc+17, loc+23 ) + "," +
					// line.substring(loc+23, loc+28 );
					// System.out.println( name + ":" + date);
	
					// find some useful information at the end of the string
					loc = line.indexOf("<br/><br/></td><td valign=\"top\">");
					String temp = line.substring(loc + 32, line.length());
					// System.out.println(temp);
					// such as date of opinion .. found by regex
					// also the title of the case .. now stored in tempa[0]
//					String[] tempa = temp.split("\\b\\d{1,2}[/]\\d{1,2}[/]\\d{2}");
					String[] tempa = temp.split("\\b.{1,2}[/].{1,2}[/].{2}");
					String opinionDate = null;
					String court = null;
					if (tempa.length == 2) {
						// get out the date of
						opinionDate = temp.substring(tempa[0].length(),temp.length() - tempa[1].length());
						// and the court designation
						court = tempa[1].trim();
						if ( court.toLowerCase().contains("&nbsp;") ) {
							court = court.substring(0, court.toLowerCase().indexOf("&nbsp;")).trim();
						}
						if ( court.toLowerCase().contains("</td>") ) {
							court = court.substring(0, court.toLowerCase().indexOf("</td>")).trim();
						}
					} else {
						// sometimes no court designation
						opinionDate = temp.substring(tempa[0].length());
					}
					// store all this in a class
					fileName = fileName.replace(".DOC", "");
					sopDate = opDate;
			        try {
			        	opDate = dfs.parse(opinionDate);
			        } catch (ParseException e ) {
			        	if ( sopDate == null ) {
				        	// Default to current date.
				        	// not very good, but best that can be done, I suppose.
							Calendar cal = Calendar.getInstance();
							cal.setTime(new Date());
							cal.set(Calendar.HOUR_OF_DAY, 0);
							cal.set(Calendar.MINUTE, 0);
							cal.set(Calendar.SECOND, 0);
							cal.set(Calendar.MILLISECOND, 0);
							opDate = cal.getTime();
			        	} else {
			        		opDate = sopDate;
			        	}
			        }
					SlipOpinion slipOpinion = new SlipOpinion(fileName, fileExtension, tempa[0].trim(),opDate, court);
					// test for duplicates
					if ( cases.contains(slipOpinion)) {
				    	logger.warning("Duplicate Detected:" + slipOpinion);
					} else {
						cases.add(slipOpinion);
					}
					//
				}
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		return cases;
	}

	private void saveCopyOfCaseList(Reader reader) {
/*		
		try ( BufferedWriter writer = Files.newBufferedWriter(Paths.get(caseListFile)) ) {
			char[] chars = new char[1024];
			int count;
			while ( (count = reader.read(chars)) != -1 ) {
				writer.write(chars, 0, count);
			}
			writer.close();
		} catch ( IOException ex) {
			logger.log(Level.SEVERE, null, ex);			
		}
*/
		try ( BufferedWriter writer = Files.newBufferedWriter(Paths.get(caseListFile), StandardCharsets.US_ASCII) ) {
			int b;
			while ( (b = reader.read()) != -1 ) {
				writer.write(b);
			}
			writer.close();
		} catch ( IOException ex) {
			logger.log(Level.SEVERE, null, ex);			
		}
	}
	
}
