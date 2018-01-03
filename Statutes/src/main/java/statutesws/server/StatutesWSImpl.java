package statutesws.server;

import java.util.*;

import parser.ParserInterface;
import service.StatutesWS;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesTitles;
import statutesws.ResponseArray;
import statutesws.ResponsePair;
import statutesws.StatuteKey;
import statutesws.StatuteKeyArray;
import statutesws.StatutesRootArray;
import statutesws.ReferencesWithReferences;
import statutesws.StatutesTitlesArray;

public class StatutesWSImpl implements StatutesWS {	
    private ParserInterface parserInterface;
	
    public StatutesWSImpl(ParserInterface parserInterface) {
    	this.parserInterface = parserInterface;
    }

	@Override
	public ResponseArray findStatutes(StatuteKeyArray keys) {
        ResponseArray responseArray = new ResponseArray();
        // copy results into the new list ..
        // Fill out the codeSections that these section are referencing ..
        // If possible ... 
        Iterator<StatuteKey> itc = keys.getItem().iterator();
        
        while ( itc.hasNext() ) {
        	StatuteKey key = itc.next();
//        	StatuteCitation citation = parserResults.findStatute(key);
            // This is a section
        	String code = key.getCode();
        	SectionNumber sectionNumber = new SectionNumber();
        	sectionNumber.setPosition(-1);
        	sectionNumber.setSectionNumber(key.getSectionNumber());
//            int refCount = citation.getRefCount(opinionBase.getOpinionKey());
//            boolean designated = citation.getDesignated();
        	
            if ( code != null ) {
                // here we look for the Doc Section within the Code Section Hierachary
                // and place it within the sectionReference we previously parsed out of the opinion
                StatutesBaseClass statutesBaseClass = parserInterface.findReference(code, sectionNumber );
                if ( statutesBaseClass != null ) {
                	ResponsePair responsePair = new ResponsePair();
                	responsePair.setStatuteKey(key);
                	responsePair.setStatutesBaseClass(statutesBaseClass);
                	responseArray.getItem().add(responsePair);
                }
//                Section codeSection = codeList.findCodeSection(sectionReference);
                // We don't want to keep ones that we can't map .. so .. 
            }
        }
        
        return responseArray;

    }

	@Override
	public StatutesTitlesArray getStatutesTitles() {
		StatutesTitles[] statutesTitles = parserInterface.getStatutesTitles();
		StatutesTitlesArray statutesTitlesArray = new StatutesTitlesArray();
		statutesTitlesArray.getItem().addAll(Arrays.asList(statutesTitles));
		return statutesTitlesArray;
	}

	@Override
	public StatutesRootArray getStatutes() {
		StatutesRootArray statutesRootArray = new StatutesRootArray();
		statutesRootArray.getItem().addAll(parserInterface.getStatutes());
		return statutesRootArray;
	}

	@Override
	public ReferencesWithReferences returnReferencesByTitle(String fullFacet) {
		return parserInterface.returnReferencesByTitle(fullFacet);
	}
    
}
