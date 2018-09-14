package guidedsearchweb.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import gsearch.GSearch;
import gsearch.util.Highlighter;
import gsearch.viewmodel.EntryReference;
import gsearch.viewmodel.ViewModel;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;

@ManagedBean
public class GuidedSearchController {
	private String path;
	private String term;
	private boolean fragments = false;
	private String inAll;
	private String inNot;
	private String inAny;
	private String inExact;
	private boolean termBuilt = false;
	private ViewModel viewModel;
	private boolean terminate;
	private Highlighter highlighter;
	
	public String submitFromTerm() throws UnsupportedEncodingException {
		System.out.print("submitFromTerm: ");
		checkTermUpdate();
		return getUrl();
	}

/*	
	public String submitFromDropdown() throws UnsupportedEncodingException {
		System.out.print("submitFromDropdown: ");
		checkTermUpdate();
		return getUrl();
	}
*/
	
	private void checkTermUpdate() {
		FacesContext context = FacesContext.getCurrentInstance();
		String hiddenTerm = context.getExternalContext().getRequestParameterMap().get("searchInputForm:hiddenTerm");
		printVars(hiddenTerm);
//		String clear = null;
//		boolean fragment;
		String bTerm = null;
		if ( 
			!inAll.isEmpty()
			|| !inNot.isEmpty()
			|| !inAny.isEmpty()
			|| !inExact.isEmpty()
		) {
			StringBuilder sb = new StringBuilder();
			if ( !inAll.isEmpty() ) {
				sb.append(appendOp(inAll, '+'));
			}
			if ( !inNot.isEmpty() ) {
				sb.append(appendOp(inNot,'-'));
			}
			if ( !inAny.isEmpty() ) {
				sb.append(inAny + " ");
			}
			if ( !inExact.isEmpty() ) {
				sb.append("\"" + inExact + "\"");
			}
			bTerm = sb.toString().trim();
		}
/*		
		if ( clear != null ) {
			term = null;
//			fragment = false;
		} else */ if ( bTerm != null && ( hiddenTerm == null || !bTerm.equals(hiddenTerm) && term.equals(hiddenTerm)) ) {
			term = bTerm;
		}
		
	}

    private String appendOp(String val, char op) {
    	val = val.trim();
    	if ( val.isEmpty()) return "";
    	String[] terms = val.trim().split(" ");
    	StringBuilder sb = new StringBuilder();
    	for ( String term: terms ) {
    		sb.append(op+term+" ");
    	}
    	return sb.toString().trim();
    }

    public String submitClear() throws UnsupportedEncodingException {
		System.out.print("submitClear: ");
		String hiddenTerm = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hiddenTerm");
		printVars(hiddenTerm);
		return getUrl();
	}

	public String submitFragments() throws UnsupportedEncodingException {
		System.out.print("submitFragments: ");
		String hiddenTerm = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hiddenTerm");
		printVars(hiddenTerm);
		return getUrl();
	}
	
	private String getUrl() throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(64);
		sb.append("/views/search.xhtml?faces-redirect=true");
		if ( path != null && !path.isEmpty() ) {
			sb.append("&path=");
			sb.append(URLEncoder.encode(path, StandardCharsets.UTF_8.name()));
		}
		if ( term != null && !term .isEmpty() ) {
			sb.append("&term=");
			sb.append(URLEncoder.encode(term, StandardCharsets.UTF_8.name()));
		}
		if ( fragments == true) {
			sb.append("&fragments=");
			sb.append(fragments);
		}
		return sb.toString();
	}

	private void printVars(String hiddenTerm) {
		System.out.print("path="+path);
		System.out.print(" term="+term);
		System.out.print(" fragments="+fragments);
		System.out.print(" inAll="+inAll);
		System.out.print(" inNot="+inNot);
		System.out.print(" inAny="+inAny);
		System.out.print(" inExact="+inExact);
		System.out.print(" hiddenTerm="+hiddenTerm);
		System.out.print(" termBuilt="+termBuilt);
		System.out.println();
	}
	
	private void buildTerm() {
    	if ( !termBuilt && term != null && !term.isEmpty() ) {
	    	try {
		    	String[] terms = term.split(" ");
		    	inAll = new String();
		    	inNot = new String();
		    	inAny = new String();
		    	inExact = new String();
		    	boolean ex = false;
		    	for(String t: terms) {
		    		if ( !ex && t.startsWith("+")) inAll=inAll.concat(t.substring(1) + " ");
		    		else if ( !ex && t.startsWith("-")) inNot=inNot.concat(t.substring(1) + " " );
		    		else if ( !ex && (t.startsWith("\"") && t.trim().endsWith("\"")) ) {
		    			inExact=inExact.concat(t.substring(1, t.length()-1) + " ");
		    		}
		    		else if ( !ex && t.startsWith("\"")) {
		    			inExact=inExact.concat(t.substring(1) + " ");
		    			ex = true;
		    		}
		    		else if ( ex && !t.endsWith("\"") ) {
		    			inExact=inExact.concat(t) + " ";
		    		}
		    		else if ( ex && t.endsWith("\"")) {
		    			inExact=inExact.concat(t.substring(0, t.length()-1)) + " ";
		    			ex = false;
		    		}
		    		else inAny = inAny.concat(t) + " ";
		    	}
		    	inAll = inAll.trim();
		    	inAny = inAny.trim();
		    	inNot = inNot.trim();
		    	inExact = inExact.trim();
	    	} catch ( Throwable t) {
	    		// silent exception
	    	}
    	}
		termBuilt = true;
	}

	public String getPath() {
		return path;
	}
	public String getInAll() {
		buildTerm();
		return inAll;
	}
	public void setInAll(String inAll) {
		this.inAll = inAll;
	}
	public String getInNot() {
		buildTerm();
		return inNot;
	}
	public void setInNot(String inNot) {
		this.inNot = inNot;
	}
	public String getInAny() {
		buildTerm();
		return inAny;
	}
	public void setInAny(String inAny) {
		this.inAny = inAny;
	}
	public String getInExact() {
		buildTerm();
		return inExact;
	}
	public void setInExact(String inExact) {
		this.inExact = inExact;
	}
	public void setPath(String path) throws UnsupportedEncodingException {
		this.path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) throws UnsupportedEncodingException {
		this.term = URLDecoder.decode(term, StandardCharsets.UTF_8.name());
	}
	public boolean isFragments() {
		return fragments;
	}
	public void setFragments(boolean fragments) {
		this.fragments = fragments;
	}
	public List<EntryReference> getBreadcrumb() {
		List<EntryReference> path = new ArrayList<>(); 
		if ( getViewModel().getEntries().size() == 1 ) {
			EntryReference currentEntry = getViewModel().getEntries().get(0);
			while ( currentEntry.getEntries() != null && currentEntry.getEntries().size() == 1 ) {
				path.add(currentEntry);
				currentEntry = currentEntry.getEntries().get(0);
			}
			path.add(currentEntry);
		}
		return path;
	}
	public List<EntryReference> getEntries() {
		if ( getViewModel().getEntries().size() == 1 ) {
			EntryReference currentEntry = getViewModel().getEntries().get(0);
			while ( currentEntry.getEntries() != null && currentEntry.getEntries().size() == 1 ) {
				currentEntry = currentEntry.getEntries().get(0);
			}
			return currentEntry.getEntries();
		}
		return viewModel.getEntries();
	}
	private ViewModel getViewModel() {
		if ( viewModel == null ) {
			try {
				viewModel = new GSearch(new ParserInterfaceRsCa()).handleRequest(path, term, fragments);
				if ( viewModel.getStatutesBaseClass() != null ) {
					viewModel.setStatutesBaseClass( createNewBaseClass(viewModel.getStatutesBaseClass()) );
				}
				terminate = viewModel.getState() == ViewModel.STATES.TERMINATE;
	//			recurseEntries(viewModel.getEntries());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return viewModel;
	}
	public boolean isTerminate() {
		return terminate;
	}
	public Highlighter getHighlighter() {
		if ( highlighter == null ) {
			highlighter = new Highlighter();
		}
		return highlighter;
	}

	public String fragmentsUrl(String baseURL) throws Exception {
		StringBuilder sb = new StringBuilder(baseURL);
		char firstArg = '?';
		if ( path != null ) {
			sb.append(firstArg);
			sb.append("path=");
			sb.append(path);
			firstArg = '&';
		}
		if ( term != null ) {
			sb.append(firstArg);
			sb.append("term=");
			sb.append(term);
			firstArg = '&';
		}
		if ( fragments == false ) {
			sb.append(firstArg + "fragments=true");
			firstArg = '&';
		}
		return sb.toString();
	}
	
	public String newPathUrl(String baseURL, String newPath) throws Exception {
		return UrlArgs( baseURL, newPath, term, fragments );
	}

	public String UrlArgs(String baseURL, String path, String term, boolean frag) throws Exception {
		StringBuilder sb = new StringBuilder(baseURL);
		char firstArg = '?';
		if ( path != null ) {
			sb.append(firstArg + "path="+URLEncoder.encode(path, "UTF-8" ));
			firstArg = '&';
		}
		if ( term != null ) {
			sb.append(firstArg + "term="+URLEncoder.encode(term, "UTF-8" ));
			firstArg = '&';
		}
		if ( frag != false ) {
			sb.append(firstArg + "fragments=true");
			firstArg = '&';
		}
		return sb.toString();
		
	}

	public String homeUrl() throws Exception {
		StringBuilder sb = new StringBuilder();
		char firstArg = '?';
		if (! viewModel.getTerm().isEmpty() ) {
			sb.append(firstArg + "term=" + URLEncoder.encode(term, "UTF-8"));
			firstArg = '&';
		}
		if ( viewModel.isFragments() ) {
			sb.append( firstArg + "fragments=true" );
			firstArg = '&';
		}
		return sb.toString();
	}
/*
	private void recurseEntries(List<EntryReference> entries) {
		for ( EntryReference entryReference: entries) {
			if ( entryReference.getStatutesBaseClass() != null ) {
				StatutesBaseClass cloneBaseClass = createNewBaseClass(entryReference.getStatutesBaseClass());
				entryReference.setStatutesBaseClass(cloneBaseClass);
				if ( entryReference.getEntries() != null ) {
					recurseEntries(entryReference.getEntries());
				}
			}
		}
	}
*/	
	private StatutesBaseClass createNewBaseClass(StatutesBaseClass statutesBaseClass) {
		StatutesBaseClass newBaseClass;
		if ( statutesBaseClass instanceof StatutesRoot) {
			newBaseClass = new StatutesRoot(
					statutesBaseClass.getTitle(), 
					statutesBaseClass.getShortTitle(), 
					statutesBaseClass.getFullFacet(), 
					statutesBaseClass.getStatuteRange() 
				);
		} else if (statutesBaseClass instanceof StatutesNode) { 
			newBaseClass = new StatutesNode(
					statutesBaseClass.getParent(), 
					statutesBaseClass.getFullFacet(), 
					statutesBaseClass.getPart(), 
					statutesBaseClass.getPartNumber(), 
					statutesBaseClass.getTitle(), 
					statutesBaseClass.getDepth(), 
					statutesBaseClass.getStatuteRange() 
				);
			newBaseClass.setStatuteRange(statutesBaseClass.getStatuteRange());
		} else if (statutesBaseClass instanceof StatutesLeaf) {
			newBaseClass = new StatutesLeaf(
					statutesBaseClass.getParent(), 
					statutesBaseClass.getFullFacet(), 
					statutesBaseClass.getPart(), 
					statutesBaseClass.getPartNumber(), 
					statutesBaseClass.getTitle(), 
					statutesBaseClass.getDepth(), 
					statutesBaseClass.getStatuteRange() 
					);
		} else {
			throw new IllegalStateException("Unkown StatutesBaseClass type:" + statutesBaseClass);
		}
		newBaseClass.setStatuteRange(statutesBaseClass.getStatuteRange());
		return newBaseClass;
	}


}
