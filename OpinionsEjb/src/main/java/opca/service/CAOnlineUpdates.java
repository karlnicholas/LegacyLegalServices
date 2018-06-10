package opca.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import opca.memorydb.CitationStore;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.OpinionStatuteCitation;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;
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
		//
//		onlineOpinions.remove(0);
//		onlineOpinions = onlineOpinions.subList(0, 5);
//		onlineOpinions = onlineOpinions.subList(0, 0);
		//
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
//		processAndPersistCases(onlineOpinions, caseScraper);

	}
	
	private void processAndPersistCases(List<SlipOpinion> opinions, OpinionScraperInterface opinionScraper) {
		// Create the CACodes list
//	    QueueUtility queue = new QueueUtility(compressSections);  // true is compress references within individual titles
		
		logger.info("There are " + opinions.size() + " SlipOpinions to process");
		List<ScrapedOpinionDocument> scrapedOpinionDocuments = opinionScraper.scrapeOpinionFiles(opinions);

		StatutesTitles[] codeTitles = new StatutesTitles[0]; //parserInterface.getStatutesTitles();

		Client statutesRs = new RestServicesFactory().connectStatutesRsService();
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
    		if ( logger.getLevel() == Level.FINE ) {
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
		}

		List<OpinionStatuteCitation> persistOpinionStatuteCitations = new ArrayList<>();
		List<OpinionBase> persistOpinions = new ArrayList<>();
		List<OpinionBase> mergeOpinions = new ArrayList<>();
		List<StatuteCitation> mergeStatutes = new ArrayList<>();	  	
		List<StatuteCitation> persistStatutes = new ArrayList<>();

		processOpinions(citationStore, mergeOpinions, persistOpinions);
	  	processStatutes(citationStore, mergeStatutes, persistStatutes);
		
		for( SlipOpinion slipOpinion: opinions ) {
//			slipOpinion.setOpinionCitations(null);		
//			slipOpinion.getOpinionCitations().clear();
//			OpinionBase opBase = mergeOpinions.get(0);
//			slipOpinion.getOpinionCitations().removeAll(mergeOpinions);
//			slipOpinion.getOpinionCitations().add(opBase);
    		if ( slipOpinion.getStatuteCitations() != null ) {
	    		for ( OpinionStatuteCitation statuteCitation: slipOpinion.getStatuteCitations() ) {
    				persistOpinionStatuteCitations.add(statuteCitation);
	    		}
    		}
			em.persist(slipOpinion);
			em.persist(slipOpinion.getSlipProperties());
		}

/*

		Date startTime = new Date();
		startTime = new Date();
		for(OpinionStatuteCitation opinionStatuteCitation: persistOpinionStatuteCitations) {
			em.persist(opinionStatuteCitation);
    	}
		logger.info("Persisted "+ persistOpinionStatuteCitations.size()+" opinionStatuteCitation in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
		
*/		
		
		Date startTime = new Date();
    	for(OpinionBase opinion: persistOpinions ) {
			em.persist(opinion);
    	}
		logger.info("Persisted "+persistOpinions.size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();

    	for(OpinionBase opinion: mergeOpinions ) {
			em.merge(opinion);
    	}
		logger.info("Merged "+mergeOpinions.size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();
		for(OpinionStatuteCitation opinionStatuteCitation: persistOpinionStatuteCitations) {
			em.persist(opinionStatuteCitation);
    	}
		logger.info("Persisted "+ persistOpinionStatuteCitations.size()+" opinionStatuteCitation in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();
    	for(StatuteCitation statute: persistStatutes ) {
			em.persist(statute);
    	}
		logger.info("Persisted "+persistStatutes.size()+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

		startTime = new Date();
    	for(StatuteCitation statute: mergeStatutes ) {
			em.merge(statute);
    	}
		logger.info("Merged "+mergeStatutes.size()+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");

	}

	private void deleteExistingOpinions(List<SlipOpinion> currentCopy) {
		logger.info("Deleting " + currentCopy.size() + " cases." );
		// need to fill out OpinionCitations and StatuteCitations for these opinions
// BUG		slipOpinionService.fetchCitations(currentCopy);
//			Object[] results = (Object[]) em.createNativeQuery("SELECT @@GLOBAL.tx_isolation, @@tx_isolation;").getSingleResult();
//			logger.info("Transaction Level: " + results[0] + " : " + results[1]);
		List<OpinionBase> citedOpinions = new ArrayList<>(); 
		List<Integer> opinionIds = new ArrayList<>();
		int i = 0;
		TypedQuery<OpinionBase> queryCitedOpinions = em.createNamedQuery("OpinionBase.fetchCitedOpinionsWithReferringOpinions", OpinionBase.class);
		for (SlipOpinion deleteOpinion: currentCopy) {
			opinionIds.add(deleteOpinion.getId());
			if ( ++i % 100 == 0 ) {
				citedOpinions.addAll( queryCitedOpinions.setParameter("opinionIds", opinionIds).getResultList() );
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			citedOpinions.addAll( queryCitedOpinions.setParameter("opinionIds", opinionIds).getResultList() );
		}
		
		// ugly double loop ( O(n^2) )
		// but currently only getting 350 opinions max, 
		// operationally much lower if update every night
		for ( OpinionBase citedOpinion: citedOpinions ) {
			for (SlipOpinion deleteOpinion: currentCopy) {
				citedOpinion.removeReferringOpinion(deleteOpinion);
			}
		}
/*		
do I really need this?
		// now merge citedOpinions without slipOpinion citations 
		for ( OpinionBase citedOpinion: citedOpinions ) {
			em.merge(citedOpinion);
		}
*/		
		
		opinionIds.clear();
		i = 0;
		Query queryOpinionStatuteCitations = em.createNamedQuery("OpinionStatuteCitation.deleteOpinionStatuteCitations");
		for (SlipOpinion deleteOpinion: currentCopy) {
			opinionIds.add(deleteOpinion.getId());
			if ( ++i % 100 == 0 ) {
				queryOpinionStatuteCitations.setParameter("opinionIds", opinionIds).executeUpdate();
				opinionIds.clear();
			}
		}
		if ( opinionIds.size() != 0 ) {
			queryOpinionStatuteCitations.setParameter("opinionIds", opinionIds).executeUpdate();
		}
		

		List<SlipProperties> slipProperties = em.createNamedQuery("SlipProperties.findAll", SlipProperties.class).getResultList();
		for (SlipOpinion deleteOpinion: currentCopy) {
			for ( SlipProperties slipProperty: slipProperties ) {
				if ( slipProperty.getOpinionKey().equals(deleteOpinion.getId())) {
					em.remove(slipProperty);
					break;
				}
			}
			em.remove(deleteOpinion);
		}
	}

	private void processOpinions(CitationStore citationStore,  
		List<OpinionBase> mergeOpinions, 
		List<OpinionBase> persistOpinions 
		
	) {
    	Date startTime = new Date();
		Set<OpinionBase> opinions = citationStore.getAllOpinions();
		List<OpinionKey> opinionKeys = new ArrayList<>();
		int i = 0;
		List<OpinionBase> existingOpinions = new ArrayList<>();
    	for(OpinionBase opinion: opinions ) {
    		opinionKeys.add(opinion.getOpinionKey());
    		if ( ++i % 100 == 0 ) {
    			existingOpinions.addAll(  slipOpinionService.opinionsWithReferringOpinions(opinionKeys) );
    			opinionKeys.clear();
    		}
    	}
    	if ( opinionKeys.size() != 0 ) {
    		existingOpinions.addAll(  slipOpinionService.opinionsWithReferringOpinions(opinionKeys) );
    	}
    	Collections.sort(existingOpinions);
    	OpinionBase[] existingOpinionsArray = existingOpinions.toArray(new OpinionBase[existingOpinions.size()]);
    	for(OpinionBase opinion: opinions ) {
//This causes a NPE !?!?	    		
//    		opinion.checkCountReferringOpinions();
    		// checking for opinionBase for citations
    		int idx = Arrays.binarySearch(existingOpinionsArray, opinion);
    		
    		if ( idx < 0 ) {
				persistOpinions.add(opinion);
			} else {
				OpinionBase existingOpinion = existingOpinionsArray[idx]; 
				existingOpinion.mergePersistenceFromSlipLoad(opinion);
/*				
	    		logger.fine("existingOpinion:= " 
	    				+ existingOpinion.getTitle() 
	    				+ "\n	:OpinionKey= " + existingOpinion.getOpinionKey()
	    				+ "\n	:CountReferringOpinions= " + existingOpinion.getCountReferringOpinions()
	    				+ "\n	:ReferringOpinions.size()= " + (existingOpinion.getReferringOpinions()== null?"xx":existingOpinion.getReferringOpinions().size())
	    				+ "\n	:OpinionCitations().size()= " + (existingOpinion.getOpinionCitations()== null?"xx":existingOpinion.getOpinionCitations().size())
	    			);
*/	    			
				mergeOpinions.add(existingOpinion);
			}
			logger.fine("opinion "+opinion.getOpinionKey()
				+ "\n	mergeOpinions:= " + mergeOpinions.size() 
				+ "\n	persistOpinions:= " + persistOpinions.size() 
			);

			
		}
		logger.info("Divided "+citationStore.getAllOpinions().size()+" opinions in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
    }

    private void processStatutes( 
    		CitationStore citationStore, 
    		List<StatuteCitation> mergeStatutes, 
    		List<StatuteCitation> persistStatutes
	) {
    	Date startTime = new Date();
    	Set<StatuteCitation> statutes = citationStore.getAllStatutes();
		List<StatuteKey> statuteKeys = new ArrayList<>();
		int i = 0;
		List<StatuteCitation> existingStatutes = new ArrayList<>();
    	for(StatuteCitation statuteCitation: statutes ) {
    		statuteKeys.add(statuteCitation.getStatuteKey());
    		if ( ++i % 100 == 0 ) {
    			existingStatutes.addAll(  slipOpinionService.statutesWithReferringOpinions(statuteKeys) );
    			statuteKeys.clear();
    		}
    	}
    	if ( statuteKeys.size() != 0 ) {
    		existingStatutes.addAll(  slipOpinionService.statutesWithReferringOpinions(statuteKeys) );
    	}
    	Collections.sort(existingStatutes);
    	StatuteCitation[] existingStatutesArray = existingStatutes.toArray(new StatuteCitation[existingStatutes.size()]);
		
    	int count = statutes.size();
    	for(StatuteCitation statute: statutes ) {
    		int idx = Arrays.binarySearch(existingStatutesArray, statute);
			if ( idx < 0 ) {
				persistStatutes.add(statute);
			} else {
	    		StatuteCitation existingStatute = existingStatutesArray[idx];
				existingStatute.mergeStatuteCitationFromSlipLoad(statute);
				
				mergeStatutes.add(existingStatute);
			}
    	}
		logger.info("Divided "+count+" statutes in "+((new Date().getTime()-startTime.getTime())/1000) + " seconds");
    }
}
