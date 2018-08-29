package guidedsearchweb.controller;

import java.io.IOException;
import java.net.URLEncoder;

import javax.faces.bean.ManagedBean;

import gsearch.GSearch;
import gsearch.util.Highlighter;
import gsearch.viewmodel.ViewModel;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;

@ManagedBean
public class GuidedSearchController {
	private String path;
	private String term;
	private boolean fragments;
	private ViewModel viewModel;
	private boolean terminate;
	private Highlighter highlighter;

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public boolean isFragments() {
		return fragments;
	}
	public void setFragments(boolean fragments) {
		this.fragments = fragments;
	}
	public ViewModel getViewModel() {
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
		return highlighter;
	}

	public String newPathUrl(String newPath) throws Exception {
		return UrlArgs( newPath, viewModel.getTerm(), viewModel.isFragments() );
	}
	
	public String UrlArgs(String path, String term, boolean frag) throws Exception {
		StringBuilder sb = new StringBuilder();
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
			sb.append(firstArg + "frag=true");
			firstArg = '&';
		}
		return sb.toString();
		
	}

	public String homeUrl() throws Exception {
		StringBuilder sb = new StringBuilder();
		char firstArg = '?';
		if (! viewModel.getTerm().isEmpty() ) {
			sb.append(firstArg + "term=" + URLEncoder.encode(viewModel.getTerm(), "UTF-8"));
			firstArg = '&';
		}
		if ( viewModel.isFragments() ) {
			sb.append( firstArg + "frag=true" );
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
