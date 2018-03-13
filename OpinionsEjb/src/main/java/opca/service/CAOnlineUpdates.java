package opca.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opca.memorydb.CitationStore;
import opca.model.OpinionBase;
import opca.model.SlipOpinion;
import opca.model.StatuteCitation;
import opca.parser.OpinionScraperInterface;
import opca.parser.OpinionDocumentParser;
import opca.parser.ScrapedOpinionDocument;
import opca.parser.ParsedOpinionCitationSet;
import statutes.StatutesTitles;
import statutesrs.StatutesTitlesArray;
import service.Client;

/**
 * 
 * I wonder if I can break it into individual slip opinions, or if it makes sense to do so.
 * I don't see why not, the efficiencies I was striving for were based on simply playing around.
 * 
 * @author karl
 *
 */
@Stateless
public class CAOnlineUpdates {	
	@Inject private Logger logger;
	@Inject private EntityManager em;
	@Inject private SlipOpinionService slipOpinionService;
    @EJB private OpinionViewSingleton opinionViewSingleton;
	
	public CAOnlineUpdates() {}

	// for testing only
	// hmmm
	public CAOnlineUpdates(EntityManager em) {
		slipOpinionService = new SlipOpinionService(em);
		this.em = em;
		logger = Logger.getLogger(CAOnlineUpdates.class.getName());
	}

	public void updatePostConstruct() {
        Date currentTime = new Date();
        Date lastBuildDate = opinionViewSingleton.getLastBuildDate();
        if ( lastBuildDate == null || lastBuildDate.compareTo(currentTime) < 0 ) {
            logger.info("calling postConstruct()");
            opinionViewSingleton.postConstruct();
        }
	}

	public void updateDatabase(OpinionScraperInterface caseScraper) {
 		List<SlipOpinion> onlineOpinions = caseScraper.getCaseList();
		if ( onlineOpinions == null || onlineOpinions.size() == 0 ) {
			logger.info("No cases found online: returning.");
			return;
		}
		List<SlipOpinion> currentOpinions = slipOpinionService.listSlipOpinions();
		List<SlipOpinion> currentCopy = new ArrayList<SlipOpinion>(currentOpinions);
		logger.info("Found " + currentCopy.size() + " in the database.");
		logger.info("Split Transactions" );
		
		// Determine old cases
		// remove online cases from dbCopy
		// what's left is no longer in online List
		Iterator<SlipOpinion> dbit = currentCopy.iterator();
		while ( dbit.hasNext() ) {
			SlipOpinion currentOpinion = dbit.next();
			if ( onlineOpinions.contains(currentOpinion) ) {
				dbit.remove();
			}
		}

		if( currentCopy.size() > 0 ) {
			deleteExistingOpinions(currentCopy);
		} else {
			logger.info("No cases deleted.");
		}

		// Determine new cases
		// remove already persisted cases from onlineList
		for ( SlipOpinion dbCase: currentOpinions ) {
			int idx = onlineOpinions.indexOf(dbCase);
			if ( idx >= 0 ) {
				onlineOpinions.remove(idx);
			}
		}
		if ( onlineOpinions.size() > 0 ) {
			// no retries
			processAndPersistCases(onlineOpinions, caseScraper);
		} else {
			logger.info("No new cases.");
		}
	}
	
	private void processAndPersistCases(List<SlipOpinion> opinions, OpinionScraperInterface opinionScraper) {
		// Create the CACodes list
//	    QueueUtility queue = new QueueUtility(compressSections);  // true is compress references within individual titles
		
		logger.info("There are " + opinions.size() + " SlipOpinions to process");
		List<ScrapedOpinionDocument> scrapedOpinionDocuments = opinionScraper.scrapeOpinionFiles(opinions);

		StatutesTitles[] codeTitles = new StatutesTitles[0]; //parserInterface.getStatutesTitles();

		Client statutesRs = new RestServicesService().connectStatutesRsService();
		StatutesTitlesArray statutesArray = statutesRs.getStatutesTitles();
		codeTitles = statutesArray.getItem().toArray(codeTitles);

//	    ParserInterface parserInterface = CAStatutesFactory.getInstance().getParserInterface(true);
		OpinionDocumentParser opinionDocumentParser = new OpinionDocumentParser(codeTitles);
		
		// this is a holds things in memory
		CitationStore citationStore = CitationStore.getInstance();
		citationStore.clearDB();

		// all memory
		for (ScrapedOpinionDocument scrapedOpinionDocument: scrapedOpinionDocuments ) {
			ParsedOpinionCitationSet parsedOpinionResults = opinionDocumentParser.parseOpinionDocument(scrapedOpinionDocument, scrapedOpinionDocument.getOpinionBase(), citationStore );
			// maybe someday deal with court issued modifications
    		opinionDocumentParser.parseSlipOpinionDetails((SlipOpinion) scrapedOpinionDocument.getOpinionBase(), scrapedOpinionDocument);
    		OpinionBase opinionBase = scrapedOpinionDocument.getOpinionBase();
    		citationStore.mergeParsedDocumentCitations(scrapedOpinionDocument.getOpinionBase(), parsedOpinionResults);
    		logger.fine("scrapedOpinionDocument:= " 
    				+ scrapedOpinionDocument.getOpinionBase().getTitle() 
    				+ "\n	:OpinionKey= " + opinionBase.getOpinionKey()
    				+ "\n	:CountReferringOpinions= " + opinionBase.getCountReferringOpinions()
    				+ "\n	:ReferringOpinions.size()= " + (opinionBase.getReferringOpinions()== null?"xx":opinionBase.getReferringOpinions().size())
    				+ "\n	:OpinionCitations().size()= " + (opinionBase.getOpinionCitations()== null?"xx":opinionBase.getOpinionCitations().size())
    				+ "\n	:Paragraphs().size()= " + (scrapedOpinionDocument.getParagraphs()== null?"xx":scrapedOpinionDocument.getParagraphs().size())
    				+ "\n	:Footnotes().size()= " + (scrapedOpinionDocument.getFootnotes()== null?"xx":scrapedOpinionDocument.getFootnotes().size())
    				+ "\n	:OpinionTable= " + parsedOpinionResults.getOpinionTable().size()
    				+ "\n	:StatuteTable= " + parsedOpinionResults.getStatuteTable().size()
				);
		}

		processesOpinions(citationStore);
		processesStatutes(citationStore);
		
		for( SlipOpinion slipOpinion: opinions ) {
			em.persist(slipOpinion);
		}
	}

	private void deleteExistingOpinions(List<SlipOpinion> currentCopy) {
		logger.info("Deleting " + currentCopy.size() + " cases." );
		// need to fill out OpinionCitations and StatuteCitations for these opinions
// BUG		slipOpinionService.fetchCitations(currentCopy);
//			Object[] results = (Object[]) em.createNativeQuery("SELECT @@GLOBAL.tx_isolation, @@tx_isolation;").getSingleResult();
//			logger.info("Transaction Level: " + results[0] + " : " + results[1]);
		for (SlipOpinion deleteOpinion: currentCopy) {
			// re-attach entity so lazy associations will be loaded
			deleteOpinion = em.merge(deleteOpinion);
			for( OpinionBase opinionBase: deleteOpinion.getOpinionCitations() ) {
				OpinionBase opSummary = slipOpinionService.findOpinion(opinionBase);
				Set<OpinionBase> referringOpinions = opSummary.getReferringOpinions();
				if ( referringOpinions.remove(deleteOpinion) ) {
					em.merge(opSummary);
				} else {
					System.out.println("deleteOpinion " + deleteOpinion + " not found in " + opSummary);							
				}
			}
			for ( StatuteCitation statuteCitation: deleteOpinion.getOnlyStatuteCitations() ) {
				StatuteCitation opStatute = slipOpinionService.findStatute(statuteCitation);
				// int count = opStatute.getOpinionStatuteReference(deleteOpinion).getCountReferences();
				opStatute.removeOpinionStatuteReference(deleteOpinion);
//				OpinionKey opKey = deleteOpinion.getOpinionKey();
//				if ( count <= 0 ) throw new RuntimeException("Cannot delete referring opinion: " + deleteOpinion + " " + opStatute);
//				mapReferringOpinionCount.remove(deleteOpinion);
				em.merge(opStatute);
			}
//					if (!deleteOpinion.getReferringOpinions().isEmpty()) throw new RuntimeException("referringOpinions not empty: " + deleteOpinion );
			// remove detached entity
			em.remove(deleteOpinion);
		}
	}

	private void processesOpinions(CitationStore citationStore) {
		OpinionBase[] opArray = new OpinionBase[citationStore.getAllOpinions().size()];    	
		citationStore.getAllOpinions().toArray(opArray);
		List<OpinionBase> opinions = Arrays.asList(opArray);
		List<OpinionBase> persistOpinions = new ArrayList<>();
		List<OpinionBase> mergeOpinions = new ArrayList<>();

    	Date startTime = new Date();
    	for(OpinionBase opinion: opinions ) {
//This causes a NPE !?!?	    		
//    		opinion.checkCountReferringOpinions();
    		OpinionBase existingOpinion = slipOpinionService.opinionExists(opinion);
			if ( existingOpinion == null ) {
				persistOpinions.add(opinion);
			} else {
				existingOpinion.mergePersistenceFromSlipLoad(opinion);
	    		logger.fine("existingOpinion:= " 
	    				+ existingOpinion.getTitle() 
	    				+ "\n	:OpinionKey= " + existingOpinion.getOpinionKey()
	    				+ "\n	:CountReferringOpinions= " + existingOpinion.getCountReferringOpinions()
	    				+ "\n	:ReferringOpinions.size()= " + (existingOpinion.getReferringOpinions()== null?"xx":existingOpinion.getReferringOpinions().size())
	    				+ "\n	:OpinionCitations().size()= " + (existingOpinion.getOpinionCitations()== null?"xx":existingOpinion.getOpinionCitations().size())
	    			);
				//opinion referred to itself?
//              existingOpinion.addOpinionBaseReferredFrom(opinion.getOpinionKey());
				mergeOpinions.add(existingOpinion);
			}
			logger.fine("opinion "+opinion.getOpinionKey()
				+ "\n	mergeOpinions:= " + mergeOpinions.size() 
				+ "\n	persistOpinions:= " + persistOpinions.size() 
			);

			
		}
		logger.info("Divided "+opinions.size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();
    	for(OpinionBase opinion: persistOpinions ) {
			em.persist(opinion);
    	}
		logger.info("Persisted "+opinions.size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();
    	for(OpinionBase opinion: mergeOpinions ) {
			em.merge(opinion);
    	}
		logger.info("Merged "+opinions.size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
    }

    private void processesStatutes( CitationStore citationStore ) {
		StatuteCitation[] stArray = new StatuteCitation[citationStore.getAllStatutes().size()];    	
		citationStore.getAllStatutes().toArray(stArray);
		List<StatuteCitation> statutes = Arrays.asList(stArray);
		List<StatuteCitation> persistStatutes = Collections.synchronizedList(new ArrayList<StatuteCitation>());
		List<StatuteCitation> mergeStatutes = Collections.synchronizedList(new ArrayList<StatuteCitation>());

    	int count = statutes.size();
    	Date startTime = new Date();
    	for(StatuteCitation statute: statutes ) {
    		StatuteCitation existingStatute = slipOpinionService.statuteExists(statute);
			if ( existingStatute == null ) {
				persistStatutes.add(statute);
			} else {
					existingStatute.mergeStatuteCitationFromSlipLoad(statute);
				mergeStatutes.add(existingStatute);
			}
    	}
		logger.info("Divided "+count+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

    	startTime = new Date();
    	for(StatuteCitation statute: persistStatutes ) {
				em.persist(statute);
    	}
		logger.info("Persisted "+statutes.size()+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();
    	for(StatuteCitation statute: mergeStatutes ) {
			em.merge(statute);
    	}
		logger.info("Merged "+statutes.size()+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
    }


}
