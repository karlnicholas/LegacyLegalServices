package statutesca.code;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import parser.ParserInterface;
import statutes.StatutesTitles;
import statutesrs.StatuteHierarchy;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;

/**
 * Created with IntelliJ IDEA. User: karl Date: 6/7/12 Time: 5:37 AM To change
 * this template use File | Settings | File Templates.
 */
public class CALoadStatutes implements ParserInterface {
	private final static Logger logger = Logger.getLogger( CALoadStatutes.class.getName() );
    private static final String DEBUGFILE = null; // "bpc";	// "fam";
    
	private ArrayList<StatutesRoot> statutes;
	private HashMap<String, StatutesTitles> mapStatutesToTitles;
	private StatutesParser parser;
//	private Unmarshaller unmarshaller;

    public static final String[] sectionTitles = {
        "title",
        "part",
        "division",
        "chapter",
        "article"
    };

/*
    public static String getShortTitle(String title) {
        if ( title == null ) return title;
        for (int i=0; i < patterns.length; ++i ) {
            if ( title.toLowerCase().contains(patterns[i]) )
                return patternsAbvr[i];
        }
        return title;
    }
*/    


	public CALoadStatutes() {
		mapStatutesToTitles = new HashMap<String, StatutesTitles> ();

		StatutesTitles statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("bpc");
		statutesTitles.setShortTitle("Bus. & Professions");
		statutesTitles.setCommonTitle("business and professions code");
		statutesTitles.setFullTitle("california business and professions code");
		statutesTitles.setAbvrTitles( new String[]{"bus. & prof. code"} ); 		
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("ccp");
		statutesTitles.setShortTitle("Civ. Procedure");
		statutesTitles.setCommonTitle("code of civil procedure");
		statutesTitles.setFullTitle("california code of civil procedure");
		statutesTitles.setAbvrTitles( new String[]{"code civ. proc.", "code of civ. pro."} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("civ");
		statutesTitles.setShortTitle("Civil");
		statutesTitles.setCommonTitle("civil code");
		statutesTitles.setFullTitle("california civil code");
		statutesTitles.setAbvrTitles( new String[]{"civ. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("com");
		statutesTitles.setShortTitle("Commercial");
		statutesTitles.setCommonTitle("commercial code");
		statutesTitles.setFullTitle("california commercial code");
		statutesTitles.setAbvrTitles( new String[]{"com. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("corp");
		statutesTitles.setShortTitle("Corporations");
		statutesTitles.setCommonTitle("corporations code");
		statutesTitles.setFullTitle("california corporations code");
		statutesTitles.setAbvrTitles( new String[]{"corp. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("edc");
		statutesTitles.setShortTitle("Education");
		statutesTitles.setCommonTitle("education code");
		statutesTitles.setFullTitle("california education code");
		statutesTitles.setAbvrTitles( new String[]{"ed. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("elec");
		statutesTitles.setShortTitle("Elections");
		statutesTitles.setCommonTitle("elections code");
		statutesTitles.setFullTitle("california elections code");
		statutesTitles.setAbvrTitles( new String[]{"elec. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("evid");
		statutesTitles.setShortTitle("Evidence");
		statutesTitles.setCommonTitle("evidence code");
		statutesTitles.setFullTitle("california evidence code");
		statutesTitles.setAbvrTitles( new String[]{"evid. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("fac");
		statutesTitles.setShortTitle("Agriculture");
		statutesTitles.setCommonTitle("food and agricultural code");
		statutesTitles.setFullTitle("california food and agricultural code");
		statutesTitles.setAbvrTitles( new String[]{"food & agr. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("fam");
		statutesTitles.setShortTitle("Family");
		statutesTitles.setCommonTitle("family code");
		statutesTitles.setFullTitle("california family code");
		statutesTitles.setAbvrTitles( new String[]{"fam. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("fgc");
		statutesTitles.setShortTitle("Fish & Game");
		statutesTitles.setCommonTitle("fish and game code");
		statutesTitles.setFullTitle("california fish and game code");
		statutesTitles.setAbvrTitles( new String[]{"fish & game code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("fin");
		statutesTitles.setShortTitle("Financial");
		statutesTitles.setCommonTitle("financial code");
		statutesTitles.setFullTitle("california financial code");
		statutesTitles.setAbvrTitles( new String[]{"fin. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("gov");
		statutesTitles.setShortTitle("Government");
		statutesTitles.setCommonTitle("government code");
		statutesTitles.setFullTitle("california government code");
		statutesTitles.setAbvrTitles( new String[]{"gov. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("hnc");
		statutesTitles.setShortTitle("Harbors & Nav.");
		statutesTitles.setCommonTitle("harbors and navigation code");
		statutesTitles.setFullTitle("california harbors and navigation code");
		statutesTitles.setAbvrTitles( new String[]{"har. & nav. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("hsc");
		statutesTitles.setShortTitle("Health");
		statutesTitles.setCommonTitle("health and safety code");
		statutesTitles.setFullTitle("california health and safety code");
		statutesTitles.setAbvrTitles( new String[]{"health & saf. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("ins");
		statutesTitles.setShortTitle("Insurance");
		statutesTitles.setCommonTitle("insurance code");
		statutesTitles.setFullTitle("california insurance code");
		statutesTitles.setAbvrTitles( new String[]{"ins. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("lab");
		statutesTitles.setShortTitle("Labor");
		statutesTitles.setCommonTitle("labor code");
		statutesTitles.setFullTitle("california labor code");
		statutesTitles.setAbvrTitles( new String[]{"lab. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("mvc");
		statutesTitles.setShortTitle("Military & Vets.");
		statutesTitles.setCommonTitle("military and veterans code");
		statutesTitles.setFullTitle("california military and veterans code");
		statutesTitles.setAbvrTitles( new String[]{"mil. and vet. code", "mil. & vet. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("pcc");
		statutesTitles.setShortTitle("Public Contact");
		statutesTitles.setCommonTitle("public contract code");
		statutesTitles.setFullTitle("california public contract code");
		statutesTitles.setAbvrTitles( new String[]{"pub. con. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("pen");
		statutesTitles.setShortTitle("Penal");
		statutesTitles.setCommonTitle("penal code");
		statutesTitles.setFullTitle("california penal code");
		statutesTitles.setAbvrTitles( new String[]{"pen. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("prc");
		statutesTitles.setShortTitle("Public Resources");
		statutesTitles.setCommonTitle("public resources code");
		statutesTitles.setFullTitle("california public resources code");
		statutesTitles.setAbvrTitles( new String[]{"pub. res. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("prob");
		statutesTitles.setShortTitle("Probate");
		statutesTitles.setCommonTitle("probate code");
		statutesTitles.setFullTitle("california probate code");
		statutesTitles.setAbvrTitles( new String[]{"prob. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("puc");
		statutesTitles.setShortTitle("Public Utilities");
		statutesTitles.setCommonTitle("public utilities code");
		statutesTitles.setFullTitle("california public utilities code");
		statutesTitles.setAbvrTitles( new String[]{"pub. util. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("rtc");
		statutesTitles.setShortTitle("Revenue & Tax.");
		statutesTitles.setCommonTitle("revenue and taxation code");
		statutesTitles.setFullTitle("california revenue and taxation code");
		statutesTitles.setAbvrTitles( new String[]{"rev. & tax. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("shc");
		statutesTitles.setShortTitle("Highways");
		statutesTitles.setCommonTitle("streets and highways code");
		statutesTitles.setFullTitle("california streets and highways code");
		statutesTitles.setAbvrTitles( new String[]{"st. & high. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("uic");
		statutesTitles.setShortTitle("Unemployment Ins.");
		statutesTitles.setCommonTitle("unemployment insurance code");
		statutesTitles.setFullTitle("california unemployment insurance code");
		statutesTitles.setAbvrTitles( new String[]{"unemp. ins. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("veh");
		statutesTitles.setShortTitle("Vehicle");
		statutesTitles.setCommonTitle("vehicle code");
		statutesTitles.setFullTitle("california vehicle code");
		statutesTitles.setAbvrTitles( new String[]{"veh. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("wat");
		statutesTitles.setShortTitle("Water");
		statutesTitles.setCommonTitle("water code");
		statutesTitles.setFullTitle("california water code");
		statutesTitles.setAbvrTitles( new String[]{"wat. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setFacetHead("wic");
		statutesTitles.setShortTitle("Welfare & Inst.");
		statutesTitles.setCommonTitle("welfare and institutions code");
		statutesTitles.setFullTitle("california welfare and institutions code");
		statutesTitles.setAbvrTitles( new String[]{"welf. & inst. code"} );
		mapStatutesToTitles.put( statutesTitles.getFullTitle(), statutesTitles );
	}

	/*
	 * There is a problem here. When using this method, the section numbers in  
	 * are not in consistent order. e.g.  Penal StatutesRoot 273a is before 273.1
	 * but 270a is after 270.1 -- This makes is difficult, or impossible, to determine
	 * what file a specific section number belongs to. I'm coding it so that
	 * 270a is said to come before 270.1. This is needed because the files
	 * 270-273.5 includes 273a. The file 273.8-273.88 does not include 273a.
	 * I don't know if there are other situations where this is reversed ... 
	 * I should write a utility to check everything. See ConvertToHybridXML in the
	 * opca project.
	 * ...
	 * ok, there's more. The second numerical element of the section number is not ordered numberically, but lexically.
	 * so .. 422.865 comes before 422.88
	 */
	public void loadFromRawPath(Path path) throws IOException {
		// ArrayList<File> files = new ArrayList<File>();

		List<Path> files = new ArrayList<Path>();
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
	        for (Path entry : stream) {
	            if (Files.isDirectory(entry)) 
	            	continue;
				if (entry.getFileName().toString().contains("constitution")) 
					continue;
				if ( DEBUGFILE != null ) { 
					if (!entry.getFileName().toString().contains(DEBUGFILE)) 
						continue;
				}
            	files.add(entry);
	        }
	    }
	    Charset encoding = StandardCharsets.ISO_8859_1;
		for ( Path file: files ) {
			logger.info("Processing " + file);
			loadRawFile( encoding, file );
		}

		Collections.sort( statutes );
	}

	@Override
	public boolean loadStatutes() {

		parser = new StatutesParser();
		statutes = new ArrayList<StatutesRoot>();
//		JAXBContext ctx = JAXBContext.newInstance(StatutesRoot.class);
//		unmarshaller = ctx.createUnmarshaller();
/*
		final ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
		final ClassLoader classLoader2 = this.getClass().getClassLoader();
		ClassLoader classLoader = null;
		final String resourcePath = "CaliforniaStatutes/";
		if ( classLoader1 == null ) logger.warning("classLoader1 is null");
		else classLoader = classLoader1;
		if ( classLoader2 == null ) logger.warning("classLoader2 is null");
		else classLoader = classLoader2;
*/		
		
		try {
			String resourcePath = System.getenv("californiastatutesloc");
			if ( resourcePath == null ) {
				resourcePath = "c:/users/karln/opcastorage/CaliforniaStatutes";
			}

			List<String> files = Files.readAllLines(Paths.get(resourcePath+"/files"), StandardCharsets.US_ASCII);
			for ( String file: files ) {
				Path filePath = Paths.get(resourcePath + "/" + file );
				logger.info("Processing " + filePath );
//				StatutesRoot c = (StatutesRoot) unmarshaller.unmarshal(url.openStream());
				ObjectInputStream ois = new ObjectInputStream( Files.newInputStream(filePath) );
				StatutesRoot c = (StatutesRoot)ois.readObject();
				c.rebuildParentReferences(null);
				statutes.add( c );
			}
		} catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}
		Collections.sort( statutes );
		return true;
	}		

	
	public void loadRawFile(Charset encoding, Path file) throws FileNotFoundException {
		statutes.add( parser.parse(this, encoding, file) );
	}

	public StatutesBaseClass findReference(String codeTitle, SectionNumber sectionNumber) {
		return findStatuteRoot(codeTitle).findReference( sectionNumber );
	}

	public StatutesRoot findStatuteRoot(String codeTitle) {
		
		String tempTitle = codeTitle.toLowerCase();
		StatutesTitles statutesTitles = null;
		for ( StatutesTitles t: mapStatutesToTitles.values() ) {
			if ( t.getFacetHead().equals( codeTitle ) ) {
				statutesTitles = t;
				break;
			}
		}
		if ( statutesTitles == null ) {
			throw new RuntimeException("StatutesRoot not found:" + codeTitle);
		}
		tempTitle = statutesTitles.getFacetHead();
		Iterator<StatutesRoot> ci = statutes.iterator();
		while (ci.hasNext()) {
			StatutesRoot code = ci.next();
			if (code.getFullFacet().toLowerCase().contains(tempTitle)) {
				return code;
			}
			if ( tempTitle.contains(code.getFullFacet().toLowerCase())) {
				return code;
			}
		}
/*		
		
		String tempTitle = codeTitle.toLowerCase();
		Iterator<StatutesRoot> ci = statutes.iterator();
		while (ci.hasNext()) {
			StatutesRoot code = ci.next();
			if (code.getTitle(false).toLowerCase().contains(tempTitle)) {
				return code;
			}
			if ( tempTitle.contains(code.getTitle(false).toLowerCase())) {
				return code;
			}
		}
*/		
		throw new RuntimeException("StatutesRoot not found:" + codeTitle);
	}

	@Override
	public String getShortTitle(String title) {
		return mapStatutesToTitles.get(title.toLowerCase()).getShortTitle(); 
	}

	@Override
	public String getFacetHead(String title) {
		return mapStatutesToTitles.get(title.toLowerCase()).getFacetHead(); 
	}

	@Override
	public ArrayList<StatutesRoot> getStatutes() {
		return statutes;
	}
	
	public static void main(String[] args) throws Exception {
//		logger.setLevel(Level.FINE);
		CALoadStatutes codes = new CALoadStatutes();
//		codes.loadFromRawPath(Paths.get("c:/users/karl/code"));
		codes.loadStatutes();
		// CodeParser parser = new CodeParser();
//		Path path = FileSystems.getDefault().getPath("codes/ccp_table_of_contents");
//		Path path = ;		// <--|
//		StatutesRoot c = parser.parse(path);		// <--|
		StatutesBaseClass reference = codes.findReference("California Penal Code", new SectionNumber(0, "625") );
		System.out.println(reference );
//		System.out.println( reference.getFullFacet());
	}

	@Override
	public StatutesTitles[] getStatutesTitles() {
		return mapStatutesToTitles.values().toArray(new StatutesTitles[0]);
	}


	@Override
	public Map<String, StatutesTitles> getMapStatutesToTitles() {
		return mapStatutesToTitles;
	}

	@Override
	public StatuteHierarchy getStatutesHierarchy(String fullFacet) {
		StatutesTitles[] statutesTitles = getStatutesTitles();
		String fullTitle = FacetUtils.findFullTitleFromFacet(statutesTitles, fullFacet);
		
		StatutesRoot statutesRoot = findReferenceByTitle(fullTitle);

		StatuteHierarchy rwr = new StatuteHierarchy();
		
		StatutesRoot returnStatutesRoot = new StatutesRoot(
				statutesRoot.getTitle(), 
				statutesRoot.getShortTitle(), 
				statutesRoot.getFullFacet(),
				statutesRoot.getStatuteRange()
			); 
		rwr.getStatutesPath().add(returnStatutesRoot);
//		String facetHead = FacetUtils.getFacetHeadFromRoot(statutesTitles, statutesRoot);
		
		List<StatutesBaseClass> subPaths = statutesRoot.getReferences();

		// ok .. now we are building parent paths ..
		StringTokenizer tokenizer = new StringTokenizer(fullFacet, String.valueOf(FacetUtils.DELIMITER) );
		// burn the first token
		StatutesBaseClass currentBaseClass = returnStatutesRoot; 
		String token = tokenizer.nextToken();
		while ( tokenizer.hasMoreTokens() ) {
			token = tokenizer.nextToken();
			for (StatutesBaseClass baseClass: subPaths ) {
				String subPart = FacetUtils.getBaseClassFacet(baseClass);
				if ( subPart.equals(token) ) {

					// check terminating
					StatutesLeaf cLeaf = baseClass.getStatutesLeaf();
					if ( cLeaf == null ) {
						StatutesNode entryReference = new StatutesNode(
							currentBaseClass, 
							baseClass.getFullFacet(), 
							baseClass.getPart(), 
							baseClass.getPartNumber(), 
							baseClass.getTitle(), 
							baseClass.getDepth(), 
							baseClass.getStatuteRange()
						);
//						currentBaseClass.addReference(entryReference);
						rwr.getStatutesPath().add(entryReference);
//						entries = entryReference.getEntries();
						subPaths = baseClass.getReferences();
//						viewModel.setState(STATES.TERMINATE);
//						termSection = baseClass.getStatutesLeaf();
						currentBaseClass = entryReference; 

					} else {
						StatutesLeaf entryReference = new StatutesLeaf(
							currentBaseClass,
							cLeaf.getFullFacet(), 
							cLeaf.getPart(), 
							cLeaf.getPartNumber(), 
							cLeaf.getTitle(), 
							cLeaf.getDepth(), 
							cLeaf.getStatuteRange() 
						);
						entryReference.getSectionNumbers().addAll(cLeaf.getSectionNumbers());
//						currentBaseClass.addReference(entryReference);
						rwr.getStatutesPath().add(entryReference);
//						entries = entryReference.getEntries();
						subPaths = baseClass.getReferences();
//						viewModel.setState(STATES.TERMINATE);
//						termSection = baseClass.getStatutesLeaf();
						currentBaseClass = entryReference; 
					}
					break;	// out of for loop
				}
			}
		}
	    if ( subPaths != null ) {
	    	for ( StatutesBaseClass reference: subPaths ) {
				// check terminating
				StatutesLeaf cLeaf = reference.getStatutesLeaf();
				if ( cLeaf == null ) {
					StatutesNode entryReference = new StatutesNode(
							currentBaseClass, 
							reference.getFullFacet(), 
							reference.getPart(), 
							reference.getPartNumber(), 
							reference.getTitle(), 
							reference.getDepth(), 
							reference.getStatuteRange()
						);
					rwr.getFinalReferences().add(entryReference);
//					currentBaseClass.addReference(entryReference);
				} else {
					StatutesLeaf entryReference = new StatutesLeaf(
							currentBaseClass, 
							cLeaf.getFullFacet(), 
							cLeaf.getPart(), 
							cLeaf.getPartNumber(), 
							cLeaf.getTitle(), 
							cLeaf.getDepth(), 
							cLeaf.getStatuteRange()
						);
					entryReference.getSectionNumbers().addAll(cLeaf.getSectionNumbers());
//					currentBaseClass.addReference(entryReference);
					rwr.getFinalReferences().add(entryReference);
				}
			}
	    }
	    return rwr;
	}

	@Override
	public StatutesRoot findReferenceByTitle(String title) {
		String tempTitle = title.toLowerCase();
		Iterator<StatutesRoot> ci = statutes.iterator();
		while (ci.hasNext()) {
			StatutesRoot statutesRoot = ci.next();
			if (statutesRoot.getTitle(false).toLowerCase().contains(tempTitle)) {
				return statutesRoot;
			}
			if ( tempTitle.contains(statutesRoot.getTitle(false).toLowerCase())) {
				return statutesRoot;
			}
		}
		throw new RuntimeException("StatutesRoot not found:" + title);
	}
}
