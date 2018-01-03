package api.gsearch.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import statutes.StatutesBaseClass;
import statutes.StatutesRoot;
import statutes.StatutesTitles;

public class FacetUtils {
	public static final char DELIMITER = '/';

	private static void hasDelimiter(String offender, char delimiter) {
		throw new IllegalArgumentException("delimiter character '" + delimiter
				+ "' (U+" + Integer.toHexString(delimiter)
				+ ") appears in path component \"" + offender + "\"");
	}

	public static String[] fromString(final String facetString) {
		return fromString(facetString, DELIMITER);
	}

	public static boolean facetMatch(final String fullFacet, final String partFacet) {
		String[] compp = partFacet.split(Pattern.quote(Character.toString(DELIMITER)));
		String[] compf = fullFacet.split(Pattern.quote(Character.toString(DELIMITER)));
		for (int i = 0, j=compp.length; i<j; ++i ) {
			if ( !compp[i].equals(compf[i])) return false;
		}
		return true;
	}

	public static String[] fromString(final String pathString, final char delimiter) {
		String[] comps = pathString.split(Pattern.quote(Character.toString(delimiter)));
		for (String comp : comps) {
			if (comp == null || comp.isEmpty()) {
				throw new IllegalArgumentException(
						"empty or null components not allowed: "
								+ Arrays.toString(comps));
			}
		}
		return comps;
	}

	public static String toString(String[] components) {
		return toString(components, DELIMITER);
	}

	public static String toString(String[] components, char delimiter) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < components.length; i++) {
			if (components[i].indexOf(delimiter) != -1) {
				hasDelimiter(components[i], delimiter);
			}
			sb.append(components[i]).append(delimiter);
		}
		sb.setLength(sb.length() - 1); // remove last delimiter
		return sb.toString();
	}


	public static String findFullTitleFromFacet( StatutesTitles[] statutesTitles, String fullPath ) {
		String mapValue = fullPath.substring(0, fullPath.indexOf('-')).toLowerCase();
		for ( StatutesTitles statuteTitle: statutesTitles ) {
			if ( statuteTitle.getFacetHead().equals( mapValue ) ) {
				return statuteTitle.getFullTitle();
			}
		}
		throw new RuntimeException("Statutes Not Found:" + fullPath);
	}

//	@Override
/*	
	public static String mapStatuteToFacetHead(String title) {
		StatutesRoot r = statutesWS.findReferenceByTitle(title.toLowerCase());
		return statutesWS.getMapStatutesToTitles().get(r.getTitle()).getFacetHead();
	}
*/	

//	@Override
	public static StatutesBaseClass findBaseClassByFacets(String facetHead, StatutesBaseClass baseClass, String... facet) {
		if ( facet.length == 0 ) return null;
		if ( !getBaseClassFacet(facetHead, baseClass).equals(facet[0])) return null;
		if ( facet.length == 1 ) return baseClass;
		List<StatutesBaseClass> references = baseClass.getReferences();
		for ( int i=0, j=references.size(); i<j; ++i ) {
			StatutesBaseClass tempBaseClass = references.get(i);
			StatutesBaseClass returnBaseClass = findBaseClassByFacets(facetHead, tempBaseClass, Arrays.copyOfRange(facet, 1, facet.length-1 ));
			//StatutesBaseClass tempBaseClass = references.get(i).
			if ( returnBaseClass != null ) return returnBaseClass;
		}
		return null;
	}

	// *** Facets for StatutesNode
//		@Override
		public static String[] getFullFacet(String facetHead, StatutesBaseClass baseClass) {
			if ( baseClass instanceof  StatutesRoot ) {
				return new String[] {getBaseClassFacet(facetHead, baseClass)};
			} 
			String[] pFacet = getFullFacet(facetHead, baseClass.getParent());
			String[] fFacet = new String[pFacet.length+1];
			System.arraycopy(pFacet, 0, fFacet, 0, pFacet.length);
			fFacet[fFacet.length-1] = getBaseClassFacet(facetHead, baseClass); 
			return fFacet; 		
		}

		public static String getBaseClassFacet(String facetHead, StatutesBaseClass baseClass) {
//			if ( part == null ) throw new RuntimeException("Subcode::part == null");
//			return part+'-'+partNumber;
//			if ( part != null ) return part+'-'+partNumber;
//			else
			int depth = baseClass.getDepth();
			String facet;
			if ( baseClass instanceof StatutesRoot ) {
//				facet = ((StatutesRoot)baseClass).getShortTitle() + '-' + Integer.toString(depth);
				facet = facetHead + '-' + Integer.toString(depth);
			} else {
//				facet = baseClass.getParent().getPart() 
				facet = facetHead
						+ '-' + Integer.toString(depth) 
						+ '-' + Integer.toString(baseClass.getParent().getReferences().indexOf(baseClass));
			}
			return facet;
		}
	// *** Facets for StatutesNode
		
/*
		//	@Override
		public static StatutesBaseClass findBaseClassByFacet(String facetHead, ParserInterface parser, String fullFacet) {
			return findBaseClassByFacets(facetHead, findStatutesRootByFacetHead(parser, facetHead), fullFacet );
		}
		private static StatutesRoot findStatutesRootByFacetHead(ParserInterface parser, String facetHead) {
			Iterator<StatutesRoot> si = parser.getStatutes().iterator();
			while (si.hasNext()) {
				StatutesRoot statutesRoot = si.next();
				if (statutesRoot.getFacetHead().equals(facetHead)) {
					return statutesRoot;
				}
			}
			throw new RuntimeException("Code not found:" + facetHead);
		}

*/
/*		
		public static String getFacetHeadFromRoot(StatutesTitles[] statutesTitles, StatutesRoot statutesRoot) {
			for ( StatutesTitles statutesTitle : statutesTitles ) {
				if ( statutesTitle.getShortTitle().equals(statutesRoot.getShortTitle())) return statutesTitle.getFacetHead(); 
			}
			throw new IllegalStateException("No title found for " + statutesRoot.getFacetHead());
		}
*/
}
