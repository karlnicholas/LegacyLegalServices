package opca.scraper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import opca.model.PartyAttorneyPair;
import opca.model.SlipOpinion;
import opca.parser.OpinionScraperInterface;
import opca.parser.ScrapedOpinionDocument;

public class CACaseScraper implements OpinionScraperInterface {
	private static final Logger logger = Logger.getLogger(CACaseScraper.class.getName());
	
	public final static String casesDir = "c:/users/karln/opca/opjpa/cases/";
	public String filesLoc = null;
	protected final static String caseListFile = "60days.html";
	protected final static String caseListDir = "c:/users/karln/opca/opjpa/html";
	private final static String downloadURL = "http://www.courts.ca.gov/opinions/documents/";
	private final static String baseUrl = "http://appellatecases.courtinfo.ca.gov";
	private final boolean debugFiles; 
	public static final String  mainCaseScreen = "mainCaseScreen";
	public static final String  disposition = "disposition";
	public static final String  partiesAndAttorneys = "partiesAndAttorneys";
	public static final String  trialCourt = "trialCourt";
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		
	public CACaseScraper(boolean debugFiles) {
		this.debugFiles = debugFiles;
		String filesLoc = System.getenv("filesLoc");
		if ( filesLoc != null  ) {
			this.filesLoc = filesLoc; 
		}
			
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
				saveCopyOfCase(caseListDir, caseListFile, new BufferedInputStream(bais));
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
		        	ScrapedOpinionDocument parsedDoc = parseScrapedDocument.parseScrapedDocument(slipOpinion, is);
					documents.add( parsedDoc ); 
					response.close();
					parseOpinionDetails(slipOpinion);
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
	
	public void parseOpinionDetails(SlipOpinion slipOpinion) {
    	logger.warning("parseOpinionDetails(SlipOpinion slipOpinion)");
		if ( slipOpinion.getSearchUrl() == null ) { 
	    	logger.warning("slipOpinion.getSearchUrl() is null");
			return;
		}
		boolean goodtogo = true;
		MyRedirectStrategy myRedirectStrategy = new MyRedirectStrategy();
		try ( CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(myRedirectStrategy).build() ) {
			HttpGet httpGet = new HttpGet(slipOpinion.getSearchUrl());
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles || filesLoc != null ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
					if ( filesLoc != null ) {
						saveCopyOfCaseDetail(filesLoc, slipPropertyFilename(slipOpinion.getFileName(), mainCaseScreen), new BufferedInputStream(bais));
					} else {
						saveCopyOfCaseDetail(CACaseScraper.casesDir, slipPropertyFilename(slipOpinion.getFileName(), mainCaseScreen), new BufferedInputStream(bais));
					}
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
	        	goodtogo = parseMainCaseScreenDetail(is, slipOpinion); 
	        	logger.warning("goodtogo = parseMainCaseScreenDetail(is, slipOpinion); = " + goodtogo);
				response.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
				goodtogo = false;
			}
			if ( !goodtogo ) {
				return;
			}
			if ( myRedirectStrategy.getLocation() == null ) {
				logger.warning("Search SlipOpinionDetails failed: " + slipOpinion.getFileName());
				return;
			}
			httpGet = new HttpGet(baseUrl+myRedirectStrategy.getLocation().replace(mainCaseScreen, disposition));
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles || filesLoc != null ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
					if ( filesLoc != null ) {
						saveCopyOfCaseDetail(filesLoc, slipPropertyFilename(slipOpinion.getFileName(), disposition), new BufferedInputStream(bais));
					} else {
						saveCopyOfCaseDetail(CACaseScraper.casesDir, slipPropertyFilename(slipOpinion.getFileName(), disposition), new BufferedInputStream(bais));
					}
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
	        	parseDispositionDetail(is, slipOpinion); 
				response.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
				// retry three times
			}
			httpGet = new HttpGet(baseUrl+myRedirectStrategy.getLocation().replace(mainCaseScreen, partiesAndAttorneys));
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
					if ( filesLoc != null ) {
		        		saveCopyOfCaseDetail(filesLoc, slipPropertyFilename(slipOpinion.getFileName(), partiesAndAttorneys), new BufferedInputStream(bais));
					} else {
		        		saveCopyOfCaseDetail(CACaseScraper.casesDir, slipPropertyFilename(slipOpinion.getFileName(), partiesAndAttorneys), new BufferedInputStream(bais));
					}
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
				parsePartiesAndAttorneysDetail(is, slipOpinion); 
				response.close();
			} catch (IOException ex) {
				logger.log(Level.SEVERE, null, ex);
			}
			httpGet = new HttpGet(baseUrl+myRedirectStrategy.getLocation().replace("mainCaseScreen", trialCourt));
			try ( CloseableHttpResponse response = httpClient.execute(httpGet) ) {
				InputStream is;
	        	if ( debugFiles || filesLoc != null ) {
					ByteArrayInputStream bais = CACaseScraper.convertInputStream(response.getEntity().getContent());
					if ( filesLoc != null ) {
		        		saveCopyOfCaseDetail(filesLoc, slipPropertyFilename(slipOpinion.getFileName(), trialCourt), new BufferedInputStream(bais));
					} else {
		        		saveCopyOfCaseDetail(CACaseScraper.casesDir, slipPropertyFilename(slipOpinion.getFileName(), trialCourt), new BufferedInputStream(bais));
					}
	        		bais.reset();
	        		is = bais;
	        	} else {
	        		is = response.getEntity().getContent();
	        	}
	        	parseTrialCourtDetail(is, slipOpinion);
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

	protected void parseTrialCourtDetail(InputStream is, SlipOpinion slipOpinion) {
		try {
			Document d = Jsoup.parse(is, StandardCharsets.UTF_8.name(), baseUrl);
			Elements rows = d.select("h2.bold~div.row");
			for ( Element row: rows) {
				List<Node> rowData = row.childNodes();
				if ( rowData.size() == 5 && rowData.get(1) instanceof Element && rowData.get(3) instanceof Element) {
					String nodeName = ((Element)rowData.get(1)).text().replace(" ", "").replace(":","").trim();
					String nodeValue = ((Element)rowData.get(3)).text().trim();
					if ( nodeName.equalsIgnoreCase("TrialCourtName") ) {
						slipOpinion.getSlipProperties().setTrialCourtName(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("County") ) {
						slipOpinion.getSlipProperties().setCounty(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("trialCourtCaseNumber") ) {
						slipOpinion.getSlipProperties().setTrialCourtCaseNumber(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("trialCourtJudge") ) {
						slipOpinion.getSlipProperties().setTrialCourtJudge(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("trialCourtJudgmentDate") ) {
						try {
							slipOpinion.getSlipProperties().setTrialCourtJudgmentDate(dateFormat.parse(nodeValue));
						} catch (Exception ignored) {}						
					}
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
	}

	protected void parsePartiesAndAttorneysDetail(InputStream is, SlipOpinion slipOpinion) {
		try {
			Document d = Jsoup.parse(is, StandardCharsets.UTF_8.name(), baseUrl);
			Elements rows = d.select("div#PartyList>table>tbody>tr");
			if ( rows.size() > 0 ) {
				Set<PartyAttorneyPair> partyAttorneyPairs = new HashSet<>();
				rows.remove(0);
				for ( Element row: rows) {
					Elements tds = row.select("td");
					if ( tds.size() == 2) {
						String party = tds.get(0).text();
						String attorney = tds.get(1).text();
						partyAttorneyPairs.add(new PartyAttorneyPair(slipOpinion.getSlipProperties(), party, attorney));
					}
				}
				if ( partyAttorneyPairs.size() > 0 ) {
					slipOpinion.getSlipProperties().setPartyAttorneyPairs(partyAttorneyPairs);
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
	}

	protected void parseDispositionDetail(InputStream is, SlipOpinion slipOpinion) {
		try {
			Document d = Jsoup.parse(is, StandardCharsets.UTF_8.name(), baseUrl);
			Elements rows = d.select("div#DispositionList div.row.flex-container");
			for ( Element row: rows) {
				List<Node> rowData = row.childNodes();
				if ( rowData.size() == 5 && rowData.get(1) instanceof Element && rowData.get(3) instanceof Element) {
					String nodeName = ((Element)rowData.get(1)).text().replace(" ", "").replace(":","").trim();
					String nodeValue = ((Element)rowData.get(3)).text().trim();
					if ( nodeName.equalsIgnoreCase("description") ) {
						slipOpinion.getSlipProperties().setDisposition(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("date") ) {
						try {
							slipOpinion.getSlipProperties().setDate(dateFormat.parse(nodeValue));
						} catch (Exception ignored) {}
					} else if ( nodeName.equalsIgnoreCase("dispositionType") ) {
						slipOpinion.getSlipProperties().setDispositionDescription(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("publicationStatus") ) {
						slipOpinion.getSlipProperties().setPublicationStatus(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("author") ) {
						slipOpinion.getSlipProperties().setAuthor(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("participants") ) {
						slipOpinion.getSlipProperties().setParticipants(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("caseCitation") ) {
						slipOpinion.getSlipProperties().setCaseCitation(nodeValue);
					}
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
	}

	protected boolean parseMainCaseScreenDetail(InputStream is, SlipOpinion slipOpinion) {
		boolean goodtogo = false;
		try {
			Document d = Jsoup.parse(is, StandardCharsets.UTF_8.name(), baseUrl);
			Elements rows = d.select("h2.bold~div.row");
			for ( Element row: rows) {
				List<Node> rowData = row.childNodes();
				if ( rowData.size() == 5 && rowData.get(1) instanceof Element && rowData.get(3) instanceof Element) {
					String nodeName = ((Element)rowData.get(1)).text().replace(" ", "").replace(":","").trim();
					String nodeValue = ((Element)rowData.get(3)).text().trim();
					if ( nodeName.equalsIgnoreCase("trialCourtCase") ) {
						slipOpinion.getSlipProperties().setTrialCourtCase(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("caseCaption") ) {
						slipOpinion.getSlipProperties().setCaseCaption(nodeValue);
						goodtogo = true;
					} else if ( nodeName.equalsIgnoreCase("division") ) {
						slipOpinion.getSlipProperties().setDivision(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("caseType") ) {
						slipOpinion.getSlipProperties().setCaseType(nodeValue);
					} else if ( nodeName.equalsIgnoreCase("filingDate") ) {
						try {
							slipOpinion.getSlipProperties().setFilingDate(dateFormat.parse(nodeValue));
						} catch (Exception ignored) {}
					} else if ( nodeName.equalsIgnoreCase("completionDate") ) {
						try {
							slipOpinion.getSlipProperties().setCompletionDate(dateFormat.parse(nodeValue));
						} catch (Exception ignored) {}
					}
				}
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
			goodtogo = false;
		}
		return goodtogo;
	}

	public static ByteArrayInputStream convertInputStream(InputStream inputStream) {
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
		DateFormat dfs = DateFormat.getDateInstance(DateFormat.SHORT);
		Date sopDate;
		Date opDate = null;
		try {
			Document doc = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "http://www.courts.ca.gov/");
			Elements trs = doc.select("table>tbody>tr");
			for ( Element row: trs) {
				Elements tds = row.getElementsByTag("td");
				if ( tds.size() != 3 )
					continue;
				
				String temp = tds.get(2).text().replace("Case Details", "").replace("\u00a0", "").trim();
				String[] tempa = temp.split("\\b.{1,2}[/].{1,2}[/].{2}");
				String opinionDate = null;
				String court = null;
				if (tempa.length == 2) {
					// get out the date of
					opinionDate = temp.substring(tempa[0].length(),temp.length() - tempa[1].length());
					// and the court designation
					court = tempa[1].trim();
				} else {
					// sometimes no court designation
					opinionDate = temp.substring(tempa[0].length());
				}
				// store all this in a class
				sopDate = opDate;
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
		        try {
		        	opDate = dfs.parse(opinionDate);
		        } catch (ParseException e ) {
		        	if ( sopDate == null ) {
			        	// Default to current date.
			        	// not very good, but best that can be done, I suppose.
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);
						opDate = cal.getTime();
		        	} else {
		        		opDate = sopDate;
		        	}
		        }
	    		Calendar parsedDate = Calendar.getInstance();
	    		parsedDate.setTime(opDate);
	    		// test to see if year out of whack.
	    		if ( parsedDate.get(Calendar.YEAR) > cal.get(Calendar.YEAR) ) {
	    			parsedDate.set(Calendar.YEAR, cal.get(Calendar.YEAR));
	    			opDate = parsedDate.getTime();
	    		}
				
				String fileName = tds.get(1).text().replace("[PDF]", "").replace("[DOC]", "").trim();
				String fileExtension = ".DOCX"; 
				Element td1 = tds.get(1);
				Elements as = td1.getElementsByTag("a");
				for ( Element a: as) {
					if (a.attr("href").toUpperCase().contains(".DOC")) {
						String line = a.attr("href").toUpperCase();
						int loc = line.indexOf(fileExtension);
						if ( loc == -1) {
							fileExtension = ".DOC";
						}
						break;
					}
				}
				as = tds.get(2).getElementsByTag("a");
				String searchUrl = null;
				for ( Element a: as) {
					if ( a.text().equalsIgnoreCase("case details" )) {
						searchUrl = a.attr("href");
					}
				}
				// fill out the title, date, and court with details later
				SlipOpinion slipOpinion = new SlipOpinion(fileName, fileExtension, tempa[0].trim(), opDate, court, searchUrl);
				// test for duplicates
				if ( cases.contains(slipOpinion)) {
			    	logger.warning("Duplicate Detected:" + slipOpinion);
				} else {
					cases.add(slipOpinion);
				}
			}
			
		} catch (IOException e) {
	    	logger.log(Level.SEVERE, null, e);
		}
		return cases;
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
	
	public static String slipPropertyFilename(String slipOpinionName, String propertyName) {
		return slipOpinionName+"-" + propertyName+ ".html";		
	}
	
}

/*			
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
//			String[] tempa = temp.split("\\b\\d{1,2}[/]\\d{1,2}[/]\\d{2}");
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
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
	        try {
	        	opDate = dfs.parse(opinionDate);
	        } catch (ParseException e ) {
	        	if ( sopDate == null ) {
		        	// Default to current date.
		        	// not very good, but best that can be done, I suppose.
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					opDate = cal.getTime();
	        	} else {
	        		opDate = sopDate;
	        	}
	        }
    		Calendar parsedDate = Calendar.getInstance();
    		parsedDate.setTime(opDate);
    		// test to see if year out of whack.
    		if ( parsedDate.get(Calendar.YEAR) > cal.get(Calendar.YEAR) ) {
    			parsedDate.set(Calendar.YEAR, cal.get(Calendar.YEAR));
    			opDate = parsedDate.getTime();
    		}
    		// get searchUrl
			loc = line.indexOf("http://appellatecases.courtinfo.ca.gov/search/");
			int loce = line.indexOf("\" target=\"_blank\">Case Details");
			String searchUrl = null;
			try {
				searchUrl = line.substring(loc, loce);
			} catch (Exception ex ) {
		    	logger.warning("No Case Details: " + fileName);
			}
    		
			SlipOpinion slipOpinion = new SlipOpinion(fileName, fileExtension, tempa[0].trim(),opDate, court, searchUrl);
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
*/		
