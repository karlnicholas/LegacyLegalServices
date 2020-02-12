package statutesaz.statutesapi;

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
import statutesaz.code.FacetUtils;
import statutes.SectionNumber;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;

/**
 * Must call loadStatutes once and only once after construction.
 * 
 */
public class AZStatutesApiImpl implements IStatutesApi {
	private final static Logger logger = Logger.getLogger( AZStatutesApiImpl.class.getName() );
    
	private ArrayList<StatutesRoot> statutes;
	private HashMap<String, StatutesTitles> mapStatutesToTitles;
	public AZStatutesApiImpl() {
		mapStatutesToTitles = new HashMap<String, StatutesTitles> ();

		StatutesTitles statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("GPC");
		statutesTitles.setShortTitle("General Provisions");
		statutesTitles.setTitle("General Provisions");
		statutesTitles.setAbvrTitles( new String[]{"General Provisions"} ); 		
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("AGC");
		statutesTitles.setShortTitle("Agriculture");
		statutesTitles.setTitle("Agriculture");
		statutesTitles.setAbvrTitles( new String[]{"Agriculture"} ); 		
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("ABC");
		statutesTitles.setShortTitle("Alcoholic Beverages");
		statutesTitles.setTitle("Alcoholic Beverages");
		statutesTitles.setAbvrTitles( new String[]{"Alcoholic Beverages"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("ASC");
		statutesTitles.setShortTitle("Amusements and Sports");
		statutesTitles.setTitle("Amusements and Sports");
		statutesTitles.setAbvrTitles( new String[]{"Amusements and Sports"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("BFI");
		statutesTitles.setShortTitle("Banks and Financial Institutions");
		statutesTitles.setTitle("Banks and Financial Institutions");
		statutesTitles.setAbvrTitles( new String[]{"Banks and Financial Institutions"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("BON");
		statutesTitles.setShortTitle("Bonds");
		statutesTitles.setTitle("Bonds");
		statutesTitles.setAbvrTitles( new String[]{"Bonds"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CSC");
		statutesTitles.setShortTitle("Child Safety");
		statutesTitles.setTitle("Child Safety");
		statutesTitles.setAbvrTitles( new String[]{"Child Safety"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CTC");
		statutesTitles.setShortTitle("Cities and Towns");
		statutesTitles.setTitle("Cities and Towns");
		statutesTitles.setAbvrTitles( new String[]{"Cities and Towns"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CAC");
		statutesTitles.setShortTitle("Corporations and Associations");
		statutesTitles.setTitle("Corporations and Associations");
		statutesTitles.setAbvrTitles( new String[]{"Corporations and Associations"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("COU");
		statutesTitles.setShortTitle("Counties");
		statutesTitles.setTitle("Counties");
		statutesTitles.setAbvrTitles( new String[]{"Counties"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CCP");
		statutesTitles.setShortTitle("Courts and Civil Proceedings");
		statutesTitles.setTitle("Courts and Civil Proceedings");
		statutesTitles.setAbvrTitles( new String[]{"Courts and Civil Proceedings"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("CRI");
		statutesTitles.setShortTitle("Criminal Code");
		statutesTitles.setTitle("Criminal Code");
		statutesTitles.setAbvrTitles( new String[]{"Criminal Code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("TEP");
		statutesTitles.setShortTitle("Trusts, Estates and Protective Proceedings");
		statutesTitles.setTitle("Trusts, Estates and Protective Proceedings");
		statutesTitles.setAbvrTitles( new String[]{"Trusts, Estates and Protective Proceedings"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("EDU");
		statutesTitles.setShortTitle("Education");
		statutesTitles.setTitle("Education");
		statutesTitles.setAbvrTitles( new String[]{"Education"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("title16");
		statutesTitles.setShortTitle("Elections and Electors");
		statutesTitles.setTitle("Elections and Electors");
		statutesTitles.setAbvrTitles( new String[]{"Elections and Electors"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("GFC");
		statutesTitles.setShortTitle("Game and Fish");
		statutesTitles.setTitle("Game and Fish");
		statutesTitles.setAbvrTitles( new String[]{"Game and Fish"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("ITC");
		statutesTitles.setShortTitle("Information Technology");
		statutesTitles.setTitle("Information Technology");
		statutesTitles.setAbvrTitles( new String[]{"Information Technology"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("IRC");
		statutesTitles.setShortTitle("Initiative, Referendum and Recall");
		statutesTitles.setTitle("Initiative, Referendum and Recall");
		statutesTitles.setAbvrTitles( new String[]{"Initiative, Referendum and Recall"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("INS");
		statutesTitles.setShortTitle("Insurance");
		statutesTitles.setTitle("Insurance");
		statutesTitles.setAbvrTitles( new String[]{"Insurance"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("JUR");
		statutesTitles.setShortTitle("Juries");
		statutesTitles.setTitle("Juries");
		statutesTitles.setAbvrTitles( new String[]{"Juries"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("JMC");
		statutesTitles.setShortTitle("Justice and Municipal Courts");
		statutesTitles.setTitle("Justice and Municipal Courts");
		statutesTitles.setAbvrTitles( new String[]{"Justice and Municipal Courts"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("LAB");
		statutesTitles.setShortTitle("Labor");
		statutesTitles.setTitle("Labor");
		statutesTitles.setAbvrTitles( new String[]{"Labor"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("MDR");
		statutesTitles.setShortTitle("Marital and Domestic Relations");
		statutesTitles.setTitle("Marital and Domestic Relations");
		statutesTitles.setAbvrTitles( new String[]{"Marital and Domestic Relations"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("MAE");
		statutesTitles.setShortTitle("Military Affairs and Emergency Management");
		statutesTitles.setTitle("Military Affairs and Emergency Management");
		statutesTitles.setAbvrTitles( new String[]{"Military Affairs and Emergency Management"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("MOG");
		statutesTitles.setShortTitle("Minerals, Oil and Gas");
		statutesTitles.setTitle("Minerals, Oil and Gas");
		statutesTitles.setAbvrTitles( new String[]{"Minerals, Oil and Gas"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("TRA");
		statutesTitles.setShortTitle("Transportation");
		statutesTitles.setTitle("Transportation");
		statutesTitles.setAbvrTitles( new String[]{"Transportation"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PAR");
		statutesTitles.setShortTitle("Partnership");
		statutesTitles.setTitle("Partnership");
		statutesTitles.setAbvrTitles( new String[]{"Partnership"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("POW");
		statutesTitles.setShortTitle("Power");
		statutesTitles.setTitle("Power");
		statutesTitles.setAbvrTitles( new String[]{"Power"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PPC");
		statutesTitles.setShortTitle("Prisons and Prisoners");
		statutesTitles.setTitle("Prisons and Prisoners");
		statutesTitles.setAbvrTitles( new String[]{"Prisons and Prisoners"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("POC");
		statutesTitles.setShortTitle("Professions and Occupations");
		statutesTitles.setTitle("Professions and Occupations");
		statutesTitles.setAbvrTitles( new String[]{"Professions and Occupations"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PRO");
		statutesTitles.setShortTitle("Property");
		statutesTitles.setTitle("Property");
		statutesTitles.setAbvrTitles( new String[]{"Property"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PBI");
		statutesTitles.setShortTitle("Public Buildings and Improvements");
		statutesTitles.setTitle("Public Buildings and Improvements");
		statutesTitles.setAbvrTitles( new String[]{"Public Buildings and Improvements"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PFC");
		statutesTitles.setShortTitle("Public Finances");
		statutesTitles.setTitle("Public Finances");
		statutesTitles.setAbvrTitles( new String[]{"Public Finances"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PHS");
		statutesTitles.setShortTitle("Public Health and Safety");
		statutesTitles.setTitle("Public Health and Safety");
		statutesTitles.setAbvrTitles( new String[]{"Public Health and Safety"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PLC");
		statutesTitles.setShortTitle("Public Lands");
		statutesTitles.setTitle("Public Lands");
		statutesTitles.setAbvrTitles( new String[]{"Public Lands"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("POE");
		statutesTitles.setShortTitle("Public Officers and Employees");
		statutesTitles.setTitle("Public Officers and Employees");
		statutesTitles.setAbvrTitles( new String[]{"Public Officers and Employees"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PPN");
		statutesTitles.setShortTitle("Public Records, Printing and Notices");
		statutesTitles.setTitle("Public Records, Printing and Notices");
		statutesTitles.setAbvrTitles( new String[]{"Public Records, Printing and Notices"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("PUC");
		statutesTitles.setShortTitle("Public Utilities and Carriers");
		statutesTitles.setTitle("Public Utilities and Carriers");
		statutesTitles.setAbvrTitles( new String[]{"Public Utilities and Carriers"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("SGC");
		statutesTitles.setShortTitle("State Government");
		statutesTitles.setTitle("State Government");
		statutesTitles.setAbvrTitles( new String[]{"State Government"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("TAX");
		statutesTitles.setShortTitle("Taxation");
		statutesTitles.setTitle("Taxation");
		statutesTitles.setAbvrTitles( new String[]{"Taxation"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("TOI");
		statutesTitles.setShortTitle("Taxation of Income");
		statutesTitles.setTitle("Taxation of Income");
		statutesTitles.setAbvrTitles( new String[]{"Taxation of Income"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("TAC");
		statutesTitles.setShortTitle("Trade and Commerce");
		statutesTitles.setTitle("Trade and Commerce");
		statutesTitles.setAbvrTitles( new String[]{"Trade and Commerce"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("WAT");
		statutesTitles.setShortTitle("Waters");
		statutesTitles.setTitle("Waters");
		statutesTitles.setAbvrTitles( new String[]{"Waters"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("WEL");
		statutesTitles.setShortTitle("Welfare");
		statutesTitles.setTitle("Welfare");
		statutesTitles.setAbvrTitles( new String[]{"Welfare"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("UCC");
		statutesTitles.setShortTitle("Uniform Commercial Code");
		statutesTitles.setTitle("Uniform Commercial Code");
		statutesTitles.setAbvrTitles( new String[]{"Uniform Commercial Code"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );

		statutesTitles = new StatutesTitles();
		statutesTitles.setLawCode("STD");
		statutesTitles.setShortTitle("Special Taxing Districts");
		statutesTitles.setTitle("Special Taxing Districts");
		statutesTitles.setAbvrTitles( new String[]{"Special Taxing Districts"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
		statutesTitles = new StatutesTitles();

		statutesTitles.setLawCode("ENV");
		statutesTitles.setShortTitle("The Environment");
		statutesTitles.setTitle("The Environment");
		statutesTitles.setAbvrTitles( new String[]{"The Environment"} );
		mapStatutesToTitles.put( statutesTitles.getLawCode(), statutesTitles );
	}
	@Override
	public boolean loadStatutes() {
		statutes = new ArrayList<StatutesRoot>();
		try {
			String resourcePath = System.getenv("arizonastatutesloc");
			if ( resourcePath == null ) {
				resourcePath = "c:/users/karln/opcastorage/ArizonaStatutes";
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
//		Collections.sort( statutes );
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
		AZStatutesApiImpl codes = new AZStatutesApiImpl();
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
