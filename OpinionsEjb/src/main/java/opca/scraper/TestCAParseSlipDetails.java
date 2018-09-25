package opca.scraper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import opca.model.SlipOpinion;
import opca.parser.ScrapedOpinionDocument;

public class TestCAParseSlipDetails extends CACaseScraper {

	public TestCAParseSlipDetails(boolean debugFiles) {
		super(debugFiles);
	}
	
	@Override
	public List<SlipOpinion> getCaseList() {
		try {
			return parseCaseList(new FileInputStream( CACaseScraper.caseListDir + "/" +  CACaseScraper.caseListFile ));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<ScrapedOpinionDocument> scrapeOpinionFiles(List<SlipOpinion> opinions) {
		List<ScrapedOpinionDocument> documents = new ArrayList<ScrapedOpinionDocument>();		
		CAParseScrapedDocument parseScrapedDocument = new CAParseScrapedDocument();
		for (SlipOpinion slipOpinion: opinions ) {			
			try ( InputStream inputStream = Files.newInputStream( Paths.get(casesDir + slipOpinion.getFileName() + slipOpinion.getFileExtension())) ) {
				ScrapedOpinionDocument scrapedDocument = parseScrapedDocument.parseScrapedDocument(slipOpinion, inputStream);
				documents.add(  scrapedDocument );
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), mainCaseScreen))) 
			) {
				slipOpinion.setSummary( parseMainCaseScreenDetail(inputStream) ); 
			} catch (IOException e) {
				e.printStackTrace();
			}
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), disposition))) 
			) {
				slipOpinion.setDisposition( parseDispositionDetail(inputStream) ); 
			} catch (IOException e) {
				e.printStackTrace();
			}
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), partiesAndAttorneys))) 
			) {
				slipOpinion.setPartiesAndAttornies( parsePartiesAndAttorneysDetail(inputStream) ); 
			} catch (IOException e) {
				e.printStackTrace();
			}
			try ( InputStream inputStream = Files.newInputStream( 
					Paths.get(casesDir + slipPropertyFilename(slipOpinion.getFileName(), trialCourt))) 
			) {
				slipOpinion.setTrialCourt( parseTrialCourtDetail(inputStream) ); 
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}		
		return documents;
		
	}
}
