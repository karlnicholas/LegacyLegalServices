package opca.parser;

import java.text.BreakIterator;
import java.util.*;

import statutes.StatutesTitles;
import opca.memorydb.CitationStore;
import opca.model.*;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/29/12
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 * 
 */
public class OpinionDocumentParser {
    private String[] terms = {"section", "§" , "sections", "§§"};
    private StatutesTitles[] codeTitles;

    public OpinionDocumentParser(StatutesTitles[] codeTitles) {
    	this.codeTitles = codeTitles;
    }

    /*
     * Should be thread safe
     */
    public ParsedOpinionCitationSet parseOpinionDocument(
    	ScrapedOpinionDocument parserDocument, 
		OpinionBase opinionBase, 
		CitationStore citationStore
	) {
    	String defaultCodeSection;
    	ParsedOpinionCitationSet parserResults = new ParsedOpinionCitationSet();
        // this breaks it into sentences and paragraphs
    	// and stores them in singletons
//        readOpinion( inputStream, paragraphs, footnotes );
    	defaultCodeSection = analyzeDefaultCodes(opinionBase, parserDocument);

        // this analyzes sentences .. 
        TreeSet<StatuteCitation> codeCitationTree = new TreeSet<StatuteCitation>();
        TreeSet<OpinionBase> caseCitationTree = new TreeSet<OpinionBase>();

        parseDoc( 
    		opinionBase, 
        	parserDocument, 
    		codeCitationTree, 
    		caseCitationTree, 
    		defaultCodeSection
    	);
        // Check to see if two elements have the same section number but one was affirmative and the other was default
        // If so, then combine them and
        checkDesignatedCodeSections( codeCitationTree, opinionBase);

        collapseCodeSections( codeCitationTree, opinionBase);
        
        // What are we going to do with the citations?
        // need to use the StatuteFacade to add the to the external list
        List<StatuteCitation> statutes = new ArrayList<StatuteCitation>(codeCitationTree);
        Set<StatuteCitation> goodStatutes = new TreeSet<StatuteCitation>();
        for ( StatuteCitation statuteCitation: statutes) {
        	// forever get rid of statutes without a referenced code.
        	if( statuteCitation.getStatuteKey().getLawCode() != null ) {
/*        		
if ( !statuteCitation.toString().equals("pen:245") ) {
	continue;
}
*/
        		synchronized(citationStore) {
	                StatuteCitation existingCitation = citationStore.findStatuteByStatute(statuteCitation);
	                if ( existingCitation != null ) {
	                	OpinionStatuteCitation osr = statuteCitation.getOpinionStatuteReference(opinionBase);
	                	existingCitation.incRefCount(opinionBase, osr.getCountReferences());
	                	statuteCitation = existingCitation;
	                }
        		}

    			parserResults.putStatuteCitation(statuteCitation);
    			goodStatutes.add(statuteCitation);
        	}
        }
        
        opinionBase.addStatuteCitations(goodStatutes);
        //
        List<OpinionBase> opinions = new ArrayList<OpinionBase>(caseCitationTree);
        Set<OpinionBase> goodOpinions = new TreeSet<OpinionBase>();
        for ( OpinionBase opinionReferredTo: opinions) {
        	// forever get rid of statutes without a referenced code.
    		synchronized(citationStore) {
	        	OpinionBase existingOpinion = citationStore.findOpinionByOpinion(opinionReferredTo);
	            if ( existingOpinion != null ) {
	            	existingOpinion.addReferringOpinion(opinionBase);
	            	opinionReferredTo = existingOpinion;
	            }
    		}
        	
			parserResults.putOpinionBase(opinionReferredTo);
			goodOpinions.add(opinionReferredTo);
        }
        opinionBase.setOpinionCitations(goodOpinions);
//        parserResults.putOpinionBase(opinionBase);
        
        // Sort according to sectionReferenced
//        Collections.sort(sectionReferences);
        
        return parserResults;
    }

    private String analyzeDefaultCodes(
		OpinionBase opinionBase, 
		ScrapedOpinionDocument parserDocument
    ) {
    	String defCodeSection = null;
    	

        for ( int fi=0; fi < parserDocument.getFootnotes().size(); fi++ ) {
            String footnote = parserDocument.getFootnotes().get(fi).toLowerCase();
            defCodeSection = searchDefCodeSection( footnote, "all further" );
            if ( defCodeSection == null ) {
                defCodeSection = searchDefCodeSection( footnote, "statutory references" );
            }
            if ( defCodeSection == null ) {
                defCodeSection = searchDefCodeSection( footnote, "undesignated" );
            }
            if ( defCodeSection != null ) break;
        }

        // paragraph searching is more stringent
        // but looks like some more case testing should be done.
        if ( defCodeSection == null ) {
            for ( int pi=0; pi < parserDocument.getParagraphs().size(); pi++ ) {
                String paragraph = parserDocument.getParagraphs().get(pi).toLowerCase();
                defCodeSection = searchDefCodeSection( paragraph, "all further" );
                if ( defCodeSection == null ) {
                    defCodeSection = searchDefCodeSection( paragraph, "all statutory references" );
                }
//                if ( defCodeSection == null ) {
//                    defCodeSection = searchDefCodeSection( paragraph, "undesignated" );
//                }
                if ( defCodeSection == null ) {
                    defCodeSection = searchDefCodeSection( paragraph, "subsequent statutory references" );
                }
                 
                if ( defCodeSection != null ) break;
            }
        }
        return defCodeSection;
    }


    /*
    Some sections had the code set from the defaultCodeSection string, but nonetheless, they
    were not part of the defaultCodeSection, so this routine runs through the DocSections to see if there are
    duplicate section numbers with different code sections and the "designated" flag set differently.
    
    The designated flag gets set only when a code section name is found 
    within the same sentence that the
    actual code section number was found.
     */
    private void checkDesignatedCodeSections(TreeSet<StatuteCitation> citations, OpinionBase opinionBase) {
    	StatuteCitation[] acitations = citations.toArray(new StatuteCitation[0]);
        for ( int idx = 0; idx < acitations.length; ++idx ) {
            if ( acitations[idx].getStatuteKey().getLawCode() != null ) {
                for ( int idx2 = idx+1; idx2 < acitations.length; ++idx2 ) {
                    // if the code is not the same
                    if ( ! (acitations[idx].getStatuteKey().getLawCode().equals( acitations[idx2].getStatuteKey().getLawCode() )) ) {
                        // but the section number is .. then
                        if ( acitations[idx].getStatuteKey().getSectionNumber().equals( acitations[idx2].getStatuteKey().getSectionNumber() ) ) {
                            // if there is a difference in designated flags ..
                            if ( acitations[idx].getDesignated() != acitations[idx2].getDesignated() ) {
                                // then compress them ..
                                if ( acitations[idx].getDesignated() == true ) {
                                	acitations[idx].setRefCount(opinionBase, 
                            			acitations[idx].getOpinionStatuteReference(opinionBase).getCountReferences() 
                            			+ acitations[idx2].getOpinionStatuteReference(opinionBase).getCountReferences()
                        			);
                                	acitations[idx2].setRefCount(opinionBase, 0);
                                    // or .. and .. get rid of it ..
                                	citations.remove(acitations[idx2]);
                                } else {
                                	acitations[idx2].setRefCount(opinionBase, 
                            			acitations[idx2].getOpinionStatuteReference(opinionBase).getCountReferences() 
                            			+ acitations[idx].getOpinionStatuteReference(opinionBase).getCountReferences()
                        			);
                                	acitations[idx].setRefCount(opinionBase, 0);
                                    // or .. and .. get rid of it ..
                                	citations.remove(acitations[idx]);
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    /*
    take out the dcs's that have null code parts and merge them into dcs's that have the same SectionNumbers
    merging means to add the two refCounts;
     */
    private void collapseCodeSections(TreeSet<StatuteCitation> citations, OpinionBase opinionBase) {
    	StatuteCitation[] acitations = citations.toArray(new StatuteCitation[0]);
        for ( int idx = 0; idx < acitations.length; ++idx ) {
            if ( acitations[idx].getStatuteKey().getLawCode() == null ) {
                for ( int idx2 = idx+1; idx2 < acitations.length; ++idx2 ) {
                    if ( acitations[idx].getStatuteKey().getSectionNumber().equals( acitations[idx2].getStatuteKey().getSectionNumber() ) ) {
                    	acitations[idx2].setRefCount(opinionBase, 
                			acitations[idx2].getOpinionStatuteReference(opinionBase).getCountReferences() 
                			+ acitations[idx].getOpinionStatuteReference(opinionBase).getCountReferences()
            			);
                    	acitations[idx].setRefCount(opinionBase, 0);
                        // or
                    	citations.remove(acitations[idx]);
                        break;
                    }
                }
            }
        }

    }

    private String searchDefCodeSection( String contents, String searchString ) {

        String nsearch = contents.toLowerCase();
        if ( nsearch.contains(searchString)) {
            int idxAF = nsearch.indexOf(searchString);
            int idxAFE = idxAF + 11;
            BreakIterator sIterator =
                    BreakIterator.getSentenceInstance(Locale.ENGLISH);

            if ( idxAF - 20 < 0 ) {
                idxAF = 0;
            } else {
                idxAF = idxAFE - 20;
            }
            if ( idxAFE + 100  > nsearch.length() ) {
                idxAFE = nsearch.length();
            } else {
                idxAFE = idxAFE + 100;
            }

            sIterator.setText(contents.substring(idxAF, idxAFE));
            int sStart = sIterator.preceding(11) + idxAF;
            int sEnd = sIterator.following(11) + idxAF;

            String sentence = nsearch.substring(sStart, sEnd );
            // see if there is anything to look for ..
            if ( !sentence.contains("code") ) return null;
            // Lets just try the hard code approach for now ..

            for ( int idx = 0, len = codeTitles.length; idx < len; ++idx ) {
                if ( sentence.contains(codeTitles[idx].getTitle().toLowerCase())) {
                    // found a hit ... lets see if it is in a good place ...
//                    System.out.println("Found: " + searchString + ":" + StaticCodePatterns.patterns[idx] + ":" + sentence);
                    return new String(codeTitles[idx].getLawCode());
                }
                for ( int aIdx = 0, aLen = codeTitles[idx].getAbvrTitles().length; aIdx < aLen; ++aIdx ) {
	                if ( sentence.contains(codeTitles[idx].getAbvrTitle(aIdx))) {
	                    // found a hit ... lets see if it is in a good place ...
	//                    System.out.println("Found: " + searchString + ":" + StaticCodePatterns.patterns[idx] + ":" + sentence);
	                    return new String(codeTitles[idx].getLawCode());
	                }
                }
            }
        }
        return null;
    }

    private void parseDoc(
    	OpinionBase opinionBase, 
    	ScrapedOpinionDocument parserDocument,
		TreeSet<StatuteCitation> codeCitationTree, 
        TreeSet<OpinionBase> caseCitationTree, 
        String defaultCodeSection
	) {
        
//try {
//sentWrit = new PrintWriter(new BufferedWriter(new FileWriter("myfile.txt", true)));
//} catch ( Exception e ) {
//	System.out.println(e);
//}
        SentenceParser sentenceParser = new SentenceParser();
        Iterator<String> pit = parserDocument.getParagraphs().iterator();
        
        while ( pit.hasNext() ) {

        	String paragraph = pit.next();
        	ArrayList<String> sentences = sentenceParser.stripSentences(paragraph);

	        for ( int si=0, sl=sentences.size(); si < sl; ++si ) {
	            String sentence = sentences.get(si).toLowerCase();
//if ( sentWrit != null ) {
//	sentWrit.println("   " + sentence);
//	sentWrit.flush();
//}
	            parseSentence(opinionBase, sentence, codeCitationTree, caseCitationTree, defaultCodeSection);
	        }
        }
//        System.out.println( disposition + ":" + summaryParagraph );

//sentWrit.close();
    }
    
    private void parseSentence(
    	OpinionBase opinionBase, 
		String sentence, 
		TreeSet<StatuteCitation> codeCitationTree, 
		TreeSet<OpinionBase> caseCitationTree, 
		String defaultCodeSection
	) {
//        System.out.println("--- Sent:" + sentence);

        ArrayList<Integer> offsets = searchForSection(sentence);
//        if ( offsets.size() > 0 ) System.out.println("Section:" + offsets.size());
        
        Iterator<Integer> oi = offsets.iterator();
        while ( oi.hasNext() ) {
            int offset = oi.next().intValue();
            StatuteCitation citation = parseCitation(opinionBase, offset, terms[0], sentence, defaultCodeSection);
            if ( citation != null ) {
//if ( sentWrit != null ) sentWrit.println("---"+citation);
            	addCodeCitationToTree(citation, codeCitationTree, opinionBase);
            }
        }

        offsets = searchForSSymbol(sentence);
//        if ( offsets.size() > 0 ) System.out.println("SSymbol:" + offsets.size());
        oi = offsets.iterator();
        while ( oi.hasNext() ) {
            int offset = oi.next().intValue();
            StatuteCitation citation = parseSSymbol(opinionBase, offset, terms[1], sentence, defaultCodeSection);
            if ( citation != null ) {
//if ( sentWrit != null ) sentWrit.println("---"+citation);
            	addCodeCitationToTree(citation, codeCitationTree, opinionBase);
            }
        }

        offsets = searchForSections(sentence);
//        if ( offsets.size() > 0 ) System.out.println("Sections:" + offsets.size());
        oi = offsets.iterator();
        while ( oi.hasNext() ) {
            int offset = oi.next().intValue();
            StatuteCitation citation = parseCitation(opinionBase, offset, terms[2], sentence, defaultCodeSection);
            if ( citation != null ) {
//if ( sentWrit != null ) sentWrit.println("---"+citation);
            	addCodeCitationToTree(citation, codeCitationTree, opinionBase);
            }
        }

        offsets = searchForSSSymbol(sentence);
//        if ( offsets.size() > 0 ) System.out.println("SSSymbol:" + offsets.size());
        oi = offsets.iterator();
        while ( oi.hasNext() ) {
            int offset = oi.next().intValue();
            StatuteCitation citation = parseSSymbol(opinionBase, offset, terms[3], sentence, defaultCodeSection);
            if ( citation != null ) {
//if ( sentWrit != null ) sentWrit.println("---"+citation);
            	addCodeCitationToTree(citation, codeCitationTree, opinionBase);
            }
        }

        offsets = searchForCases(sentence);
		oi = offsets.iterator();
		while ( oi.hasNext() ) {
			int offset = oi.next().intValue();
			OpinionBase opinion = parseCase(opinionBase, offset, sentence);
			if ( opinion != null ) {
//if ( sentWrit != null ) sentWrit.println("---"+citation);
				// add a referring OpinionBaseKey
				addCaseCitation(opinion, caseCitationTree);
			}
		}
    }

    public void parseSlipOpinionDetails(
        	SlipOpinion slipOpinion, 
        	ScrapedOpinionDocument parserDocument
    	) {
        
        SentenceParser sentenceParser = new SentenceParser();
        Iterator<String> pit = parserDocument.getParagraphs().iterator();
        boolean summaryFound = false;
        
        while ( pit.hasNext() ) {

        	String paragraph = pit.next();
        	ArrayList<String> sentences = sentenceParser.stripSentences(paragraph);

        	// look for details
        	// after a summaryParagraph is found, don't check any further .. (might have to change) 
        	if ( !summaryFound ) {
        		summaryFound = checkDetails( slipOpinion, paragraph, sentences );
        	}
        }
    }

    private boolean checkDetails(
    	SlipOpinion slipOpinion, 
    	String paragraph, 
    	ArrayList<String> sentences 
    ) {
    	String trimmed = paragraph.trim();
    	String lower = trimmed.toLowerCase();
    	boolean processed = false;
		if ( (lower.contains("affirm") || lower.contains("reverse") ) 
				&& !(paragraph.trim().startsWith("appeal from") || paragraph.trim().startsWith("appeals from")) 
		) {
			processed = true;
			Iterator<String> sit = sentences.iterator();
			while (sit.hasNext()) {
				String sentence = sit.next().trim().toLowerCase();
				
				if ( (sentence.contains("affirm") || sentence.contains("reverse") ) 
					&& ( sentence.contains("we ")) 
				) {					
					// get five sentences ... 
					slipOpinion.getSlipProperties().setSummary( paragraph
							.replace('\n', ' ')
							.replace('\r', ' ')
							.replace('\u0002', ' ')
			                .replace('\u201D', '"')
			                .replace('\u201C', '"')
			                .replace('\u2018', '\'')
			                .replace('\u2019', '\'')
			                .replace('\u001E', '-')
							.replace('\u00A0', ' ')
							.replace('\u000B',  ' ')
						);
						
					StringTokenizer tok = new StringTokenizer(sentence);
					while (tok.hasMoreTokens()) {
						String token = tok.nextToken();
						token = token.replace(",", "").replace(".", "");
						if ( token.contains("affirm") || token.contains("reverse") ) {
							if ( token.contains("ed") ) {
								slipOpinion.getSlipProperties().setSummary(null);
							} else {
								StringBuffer disposition = new StringBuffer(token.trim().replace(",", "").replace(".", "").toLowerCase());
								disposition.setCharAt(0, disposition.substring(0, 1).toUpperCase().charAt(0));
								if ( slipOpinion.getSlipProperties().getDisposition() == null ) {
									slipOpinion.getSlipProperties().setDisposition( disposition.toString() );
								}
							}
							break;
						}	
					}
				}
			}
		}
		return processed;
    }

    private void addCaseCitation( OpinionBase opinion, TreeSet<OpinionBase> caseCitationTree ) {
        if ( !caseCitationTree.contains(opinion) ) {
        	caseCitationTree.add(opinion);
        }
    }

    private void addCodeCitationToTree( 
    		StatuteCitation statuteCitation, 
    		TreeSet<StatuteCitation> codeCitationTree, 
    		OpinionBase opinionBase 
	) {
    	
        if ( codeCitationTree.contains(statuteCitation) ) {
        	StatuteCitation statuteCitationFloor = codeCitationTree.floor(statuteCitation);
        	statuteCitationFloor.incRefCount(opinionBase, 1);
        } else {
        	codeCitationTree.add(statuteCitation);
        }
    }

    // section or sections <-- plural
    private OpinionBase parseCase(OpinionBase opinionBase, int offset, String sentence ) {
    	int startPos = offset;
    	int endPos = offset+5;
    	int sentEnd = sentence.length();
    	if ( endPos >= sentEnd ) return null;
    	// first find .2nd .3d, or .4th  (around ' cal.' ) 
    	// what about .supp ? 
    	while ( !Character.isWhitespace(sentence.charAt(endPos))) {
    		endPos++;
    		if ( endPos >= sentEnd ) break;
    	}
    	// skip next whitespace
    	if ( endPos + 1 < sentEnd && Character.isWhitespace(sentence.charAt(endPos)) ) {
    		endPos = endPos + 1;
    	}
    	// check for ' at p. '
    	if ( endPos + 6 < sentEnd && sentence.substring(endPos, endPos + 6).equals("at p. ")) {
    		endPos = endPos + 6;
    		return null;
    	}
    	// check for ' at pp. '
    	if ( endPos + 7 < sentEnd && sentence.substring(endPos, endPos + 7).equals("at pp. ")) {
    		endPos = endPos + 7;
    		return null;
    	}
    	// do while endPos isDigit
    	while ( endPos < sentEnd && Character.isDigit(sentence.charAt(endPos))) {
    		endPos++;
    		if ( endPos >= sentEnd ) break;
    	}
    	// endPos should be set, do startPos
    	while ( startPos > 0 && Character.isDigit(sentence.charAt(startPos-1))) {
    		startPos = startPos-1;
    	}

    	// sanity checks.
    	String caseCite = sentence.substring(startPos, endPos);
    	String[] parts = caseCite.split(" ");
    	if ( parts.length != 3 ) return null;
    	if ( parts[0].length() == 0 ) return null;
    	if ( parts[2].length() == 0 ) return null;
    	for ( String appellateSet: OpinionKey.appellateSets ) {
        	if ( parts[1].equalsIgnoreCase(appellateSet) ) {
        		return new OpinionBase(opinionBase, parts[0], parts[1], parts[2]);
        	}
    	}
        return null;
    }

    // section or sections <-- plural
    private StatuteCitation parseCitation(
    	OpinionBase opinionBase, 
		int offset, 
		String term, 
		String sentence, 
    	String defaultCodeSection
	) {

    	StatuteCitation citation = null;

        String sectionNumber = parseSectionNumber(offset, sentence );

        if ( sectionNumber == null  ) return null;

        // a little more cleanup ...
        //        sectionNumber = sectionNumber.replace(',', ' ').trim();
        sectionNumber = sectionNumber.trim();
        if ( sectionNumber.charAt(sectionNumber.length()-1) == '.') {
            sectionNumber = sectionNumber.substring(0, sectionNumber.length()-1);
        }
        if ( Character.isDigit(sectionNumber.charAt(0)) ) {
            // time to look for a Code names ...
            String title = findTitle(sentence, offset, term, sectionNumber);

            //        System.out.println("\n===============:" + code + ":" + sectionNumber + ":" + hitSpot + "\n" + hit);

            // make a DocCodeSection out of these things ..
            citation = new StatuteCitation(opinionBase, title, new String( sectionNumber) );
            if ( title == null && defaultCodeSection != null ) {
            	citation.getStatuteKey().setTitle( defaultCodeSection );
            }
        }
        return citation;
    }

    private StatuteCitation parseSSymbol(
		OpinionBase opinionBase, 
    	int offset, 
    	String term, 
    	String sentence, 
    	String defaultCodeSection
    ) {

    	StatuteCitation citation = null;

        String sectionNumber = parseSectionNumber(offset, sentence);

        if ( sectionNumber == null  ) return null;

        // a little more cleanup ...
        //        sectionNumber = sectionNumber.replace(',', ' ').trim();
        sectionNumber = sectionNumber.trim();
        if ( sectionNumber.charAt(sectionNumber.length()-1) == '.') {
            sectionNumber = sectionNumber.substring(0, sectionNumber.length()-1);
        }
        if ( Character.isDigit(sectionNumber.charAt(0)) ) {
            // time to look for a Code names ...
            String code = findTitle(sentence, offset, term, sectionNumber);

            //        System.out.println("\n===============:" + code + ":" + sectionNumber + ":" + hitSpot + "\n" + hit);

            // make a DocCodeSection out of these things ..
            citation = new StatuteCitation(opinionBase, code, new String( sectionNumber) );
            if ( code == null && defaultCodeSection != null ) {
            	citation.getStatuteKey().setTitle( defaultCodeSection );
            }
        }
        return citation;
    }

    private ArrayList<Integer> searchForSection(String sentence) {
        // Search for string "section"
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        int posd = sentence.indexOf("sections", 0);
        int pos = sentence.indexOf("section", 0);
        while (pos != -1) {
            if ( posd != pos ) {
                offsets.add( new Integer(pos) );
            } else {
                posd = sentence.indexOf("sections", posd+1);
                pos = pos + 1;
            }
            pos = sentence.indexOf("section", pos+1);
        }
        return offsets;
    }

    private ArrayList<Integer> searchForSSymbol(String sentence) {
    // Do for section symbol �, and only one occurance of section symbol
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        int posd = sentence.indexOf("§§", 0);
        int pos = sentence.indexOf("§", 0);
        while (pos != -1) {
            if ( posd != pos ) {
                offsets.add( new Integer(pos) );
            } else {
                posd = sentence.indexOf("§§", posd+1);
                pos = pos + 1;
            }
            pos = sentence.indexOf("§", pos+1);
        }
        return offsets;
    }
    private ArrayList<Integer> searchForSections(String sentence) {
        // Search for string "sections"
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        int pos = sentence.indexOf("sections", 0);
        while (pos != -1) {
            offsets.add( new Integer(pos) );
            pos = sentence.indexOf("sections", pos+1);
        }
        return offsets;
    }
    private ArrayList<Integer> searchForSSSymbol(String sentence) {
        // Do for sections symbol §§
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        int pos = sentence.indexOf("§§", 0);
        while (pos != -1) {
            offsets.add( new Integer(pos) );
            pos = sentence.indexOf("§§", pos+1);
        }
        return offsets;
    }

    private ArrayList<Integer> searchForCases(String sentence) {
        // Do for sections symbol §§
        ArrayList<Integer> offsets = new ArrayList<Integer>();
        int pos = sentence.indexOf(" cal.", 0);
        while (pos != -1) {
            offsets.add( new Integer(pos) );
            pos = sentence.indexOf(" cal.", pos+1);
        }
        return offsets;
    }

    private String parseSectionNumber(int hitSpot, String sentence) {
        BreakIterator sIterator = BreakIterator.getWordInstance(Locale.ENGLISH);

        sIterator.setText(sentence.replace(',', ' '));
        // make a first good attempt at getting the section number from the next word.
        int numLoc = sIterator.following(hitSpot);
        int current = sIterator.next();
        boolean ok = false;
        while (current != BreakIterator.DONE) {
            for (int p = numLoc; p < current; p++) {
                if (Character.isLetterOrDigit(sentence.codePointAt(p))) {
                    ok = true;
                    break;
                }
            }
            if ( ok ) break;
            numLoc = current;
            current = sIterator.next();
        }
//        if ( current == BreakIterator.DONE ) return null;
        if ( current == BreakIterator.DONE ) {
            return null;
        };
//        return hit.substring(numLoc, current);
        return sentence.substring(numLoc, current);
    }

    // StaticCodePatterns.patterns, StaticCodePatterns.patterns
    private String findTitle(String sentence, int offset, String term, String sectionNumber) {

        // Lets just try the hard code approach for now ..
        for ( int idx = 0, len = codeTitles.length; idx < len; ++idx ) {
            if ( sentence.contains(codeTitles[idx].getTitle().toLowerCase())) {
                // found a hit ... lets see if it is in a good place ...
                int iCode = sentence.indexOf(codeTitles[idx].getTitle());
                int lenCode = codeTitles[idx].getTitle().length();
                boolean imp;
                do {
                	imp = false;
                	// plus one is ok because all titles have a length greater than 1
                    int nxtiCode = sentence.indexOf(codeTitles[idx].getTitle().toLowerCase(), iCode+1);
                    if ( nxtiCode != -1 ) {
                    	if ( offset - nxtiCode > 0  ) {
                    		if ( nxtiCode > iCode ) iCode = nxtiCode;
                    		imp = true;
                    	}
                    }
                } while ( imp == true);
                int close = offset - (iCode + lenCode);
                // close = 1 makes a perfect hit ..
//                System.out.println(close + ":" + nHit);
                if ( 0 < close && close < 20 ) return codeTitles[idx].getLawCode();
                // what about "of the"
                close = iCode - (offset + term.length() + sectionNumber.length() + 7 );
//                System.out.println(close);
                if ( 0 < close && close < 20 ) return codeTitles[idx].getLawCode();

//                return new String(patterns[idx]);
            } else {
                // Lets just try the hard code approach for now ..
                for ( int aIdx = 0, aLen = codeTitles[idx].getAbvrTitles().length; aIdx < aLen; ++aIdx ) {
                    if ( sentence.contains(codeTitles[idx].getAbvrTitle(aIdx))) {
                        // found a hit ... lets see if it is in a good place ...
                    	// need code to find the "closest" hit
                        int iCode = sentence.indexOf(codeTitles[idx].getAbvrTitle(aIdx));
                        int lenCode = codeTitles[idx].getAbvrTitle(aIdx).length();
                        boolean imp;
                        do {
                        	imp = false;
                        	// plus one is ok because all titles have a length greater than 1
	                        int nxtiCode = sentence.indexOf(codeTitles[idx].getAbvrTitle(aIdx), iCode+1);
	                        if ( nxtiCode != -1 ) {
	                        	if ( offset - nxtiCode > 0  ) {
	                        		if ( nxtiCode > iCode ) iCode = nxtiCode;
	                        		imp = true;
	                        	}
	                        }
                        } while ( imp == true);
                        int close = offset - (iCode + lenCode);
                        // close = 1 makes a perfect hit ..
//                        System.out.println(close + ":" + nHit);
                        if ( 0 < close && close < 20 ) return codeTitles[idx].getLawCode();
                        // what about "of the"
                        close = iCode - (offset + term.length() + sectionNumber.length() + 7 );
//                        System.out.println(close);
                        if ( 0 < close && close < 20 ) return codeTitles[idx].getLawCode();
                    }
                }
            	
            }
        }

//        return new String("aaaa code"); // will have to consider what about this case ..
        // for now treat it as if no "code" was found
        return null;
    }

}
