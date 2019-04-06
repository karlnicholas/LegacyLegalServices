package statutesca.statutesapi;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import statutes.StatutesTitles;
import statutes.api.IStatutesApi;
import statutes.service.dto.StatuteHierarchy;
import statutesca.code.FacetUtils;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;

/**
 * Must call loadStatutes once and only once after construction.
 * 
 */
public class CAStatutesApiImpl implements IStatutesApi {
	private final static Logger logger = Logger.getLogger( CAStatutesApiImpl.class.getName() );
    
	private ArrayList<StatutesRoot> statutes;
	private HashMap<String, StatutesTitles> mapStatutesToTitles;
	public CAStatutesApiImpl() {
		mapStatutesToTitles = new HashMap<String, StatutesTitles> ();

		StatutesTitles statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("BPC");
		statutesTitles.setShortTitle("Bus. & Professions");
		statutesTitles.setTitle("Business and Professions Code");
		statutesTitles.setAbvrTitles( new String[]{"bus. & prof. code"} ); 		
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CCP");
		statutesTitles.setShortTitle("Civ. Procedure");
		statutesTitles.setTitle("Code of Civil Procedure");
		statutesTitles.setAbvrTitles( new String[]{"code civ. proc.", "code of civ. pro."} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CIV");
		statutesTitles.setShortTitle("Civil");
		statutesTitles.setTitle("Civil Code");
		statutesTitles.setAbvrTitles( new String[]{"civ. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("COM");
		statutesTitles.setShortTitle("Commercial");
		statutesTitles.setTitle("Commercial Code");
		statutesTitles.setAbvrTitles( new String[]{"com. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CORP");
		statutesTitles.setShortTitle("Corporations");
		statutesTitles.setTitle("Corporations Code");
		statutesTitles.setAbvrTitles( new String[]{"corp. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("EDC");
		statutesTitles.setShortTitle("Education");
		statutesTitles.setTitle("Education Code");
		statutesTitles.setAbvrTitles( new String[]{"ed. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("ELEC");
		statutesTitles.setShortTitle("Elections");
		statutesTitles.setTitle("Elections Code");
		statutesTitles.setAbvrTitles( new String[]{"elec. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("EVID");
		statutesTitles.setShortTitle("Evidence");
		statutesTitles.setTitle("Evidence Code");
		statutesTitles.setAbvrTitles( new String[]{"evid. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("FAC");
		statutesTitles.setShortTitle("Agriculture");
		statutesTitles.setTitle("Food and Agricultural Code");
		statutesTitles.setAbvrTitles( new String[]{"food & agr. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("FAM");
		statutesTitles.setShortTitle("Family");
		statutesTitles.setTitle("Family Code");
		statutesTitles.setAbvrTitles( new String[]{"fam. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("FGC");
		statutesTitles.setShortTitle("Fish & Game");
		statutesTitles.setTitle("Fish and Game Code");
		statutesTitles.setAbvrTitles( new String[]{"fish & game code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("FIN");
		statutesTitles.setShortTitle("Financial");
		statutesTitles.setTitle("Financial Code");
		statutesTitles.setAbvrTitles( new String[]{"fin. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("GOV");
		statutesTitles.setShortTitle("Government");
		statutesTitles.setTitle("Government Code");
		statutesTitles.setAbvrTitles( new String[]{"gov. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("HNC");
		statutesTitles.setShortTitle("Harbors & Nav.");
		statutesTitles.setTitle("Harbors and Navigation Code");
		statutesTitles.setAbvrTitles( new String[]{"har. & nav. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("HSC");
		statutesTitles.setShortTitle("Health");
		statutesTitles.setTitle("Health and Safety Code");
		statutesTitles.setAbvrTitles( new String[]{"health & saf. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("INS");
		statutesTitles.setShortTitle("Insurance");
		statutesTitles.setTitle("Insurance Code");
		statutesTitles.setAbvrTitles( new String[]{"ins. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("LAB");
		statutesTitles.setShortTitle("Labor");
		statutesTitles.setTitle("Labor Code");
		statutesTitles.setAbvrTitles( new String[]{"lab. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("MVC");
		statutesTitles.setShortTitle("Military & Vets.");
		statutesTitles.setTitle("Military and Veterans Code");
		statutesTitles.setAbvrTitles( new String[]{"mil. and vet. code", "mil. & vet. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PEN");
		statutesTitles.setShortTitle("Penal");
		statutesTitles.setTitle("Penal Code");
		statutesTitles.setAbvrTitles( new String[]{"pen. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PROB");
		statutesTitles.setShortTitle("Probate");
		statutesTitles.setTitle("Probate Code");
		statutesTitles.setAbvrTitles( new String[]{"prob. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PCC");
		statutesTitles.setShortTitle("Public Contact");
		statutesTitles.setTitle("Public Contract Code");
		statutesTitles.setAbvrTitles( new String[]{"pub. con. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PRC");
		statutesTitles.setShortTitle("Public Resources");
		statutesTitles.setTitle("Public Resources Code");
		statutesTitles.setAbvrTitles( new String[]{"pub. res. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PUC");
		statutesTitles.setShortTitle("Public Utilities");
		statutesTitles.setTitle("Public Utilities Code");
		statutesTitles.setAbvrTitles( new String[]{"pub. util. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("RTC");
		statutesTitles.setShortTitle("Revenue & Tax.");
		statutesTitles.setTitle("Revenue and Taxation Code");
		statutesTitles.setAbvrTitles( new String[]{"rev. & tax. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("SHC");
		statutesTitles.setShortTitle("Highways");
		statutesTitles.setTitle("Streets and Highways Code");
		statutesTitles.setAbvrTitles( new String[]{"st. & high. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("UIC");
		statutesTitles.setShortTitle("Unemployment Ins.");
		statutesTitles.setTitle("Unemployment Insurance Code");
		statutesTitles.setAbvrTitles( new String[]{"unemp. ins. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("VEH");
		statutesTitles.setShortTitle("Vehicle");
		statutesTitles.setTitle("Vehicle Code");
		statutesTitles.setAbvrTitles( new String[]{"veh. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("WAT");
		statutesTitles.setShortTitle("Water");
		statutesTitles.setTitle("Water Code");
		statutesTitles.setAbvrTitles( new String[]{"wat. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("WIC");
		statutesTitles.setShortTitle("Welfare & Inst.");
		statutesTitles.setTitle("Welfare and Institutions Code");
		statutesTitles.setAbvrTitles( new String[]{"welf. & inst. code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		
	}
	@Override
	public boolean loadStatutes() {
		statutes = new ArrayList<StatutesRoot>();
		try {
			String resourcePath = System.getenv("californiastatutesloc");
			if ( resourcePath == null ) {
				resourcePath = "c:/users/karln/opcastorage/CaliforniaStatutes";
			}

			List<String> files = Files.readAllLines(Paths.get(resourcePath+"/files"), StandardCharsets.US_ASCII);
			for ( String file: files ) {
				Path filePath = Paths.get(resourcePath + "/" + file );
				logger.info("Processing " + filePath );
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

	public StatutesBaseClass findReference(String lawCode, SectionNumber sectionNumber) {
		return findStatuteRoot(lawCode).findReference( sectionNumber );
	}

	public StatutesRoot findStatuteRoot(String lawCode) {
		
		StatutesTitles statutesTitles = null;
		for ( StatutesTitles t: mapStatutesToTitles.values() ) {
			if ( t.getLawCode().equals( lawCode ) ) {
				statutesTitles = t;
				break;
			}
		}
		if ( statutesTitles == null ) {
			throw new RuntimeException("StatutesRoot not found:" + lawCode);
		}
		Iterator<StatutesRoot> ci = statutes.iterator();
		while (ci.hasNext()) {
			StatutesRoot code = ci.next();
			if (code.getFullFacet().contains(lawCode)) {
				return code;
			}
			if ( lawCode.contains(code.getFullFacet())) {
				return code;
			}
		}
		throw new RuntimeException("StatutesRoot not found:" + lawCode);
	}

	@Override
	public String getShortTitle(String lawCode) {
		return mapStatutesToTitles.get(lawCode).getShortTitle(); 
	}

	@Override
	public String getTitle(String lawCode) {
		return mapStatutesToTitles.get(lawCode).getTitle(); 
	}

	@Override
	public ArrayList<StatutesRoot> getStatutes() {
		return statutes;
	}
	
	public static void main(String[] args) throws Exception {
		CAStatutesApiImpl codes = new CAStatutesApiImpl();
		codes.loadStatutes();
		StatutesBaseClass reference = codes.findReference("California Penal Code", new SectionNumber(0, "625") );
		System.out.println(reference );
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
		String lawCode = FacetUtils.findLawCodeFromFacet(statutesTitles, fullFacet);
		
		StatutesRoot statutesRoot = findReferenceByLawCode(lawCode);

		StatuteHierarchy rwr = new StatuteHierarchy();
		
		StatutesRoot returnStatutesRoot = new StatutesRoot(
				statutesRoot.getLawCode(), 
				statutesRoot.getTitle(), 
				statutesRoot.getShortTitle(), 
				statutesRoot.getFullFacet(),
				statutesRoot.getStatuteRange()
			); 
		rwr.getStatutesPath().add(returnStatutesRoot);
		
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
						rwr.getStatutesPath().add(entryReference);
						subPaths = baseClass.getReferences();
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
						rwr.getStatutesPath().add(entryReference);
						subPaths = baseClass.getReferences();
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
					rwr.getFinalReferences().add(entryReference);
				}
			}
	    }
	    return rwr;
	}

	@Override
	public StatutesRoot findReferenceByLawCode(String lawCode) {
		Iterator<StatutesRoot> ci = statutes.iterator();
		while (ci.hasNext()) {
			StatutesRoot statutesRoot = ci.next();
			if (statutesRoot.getLawCode().contains(lawCode)) {
				return statutesRoot;
			}
			if ( lawCode.contains(statutesRoot.getLawCode())) {
				return statutesRoot;
			}
		}
		throw new RuntimeException("StatutesRoot not found: " + lawCode);
	}
}
