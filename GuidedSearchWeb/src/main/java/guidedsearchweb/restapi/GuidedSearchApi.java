package guidedsearchweb.restapi;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import gsearch.GSearch;
import gsearch.viewmodel.ViewModel;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;

@Path("gs")
@Produces("application/json")
public class GuidedSearchApi {

    public GuidedSearchApi() {
	}

    @GET
	public ViewModel handleRequest( @QueryParam("path") String path, @QueryParam("term") String term, @QueryParam("fragments") boolean fragments) {
		try {
			ViewModel viewModel = new GSearch(new ParserInterfaceRsCa()).handleRequest(path, term, fragments);
			if ( viewModel.getStatutesBaseClass() != null ) {
				viewModel.setStatutesBaseClass( createNewBaseClass(viewModel.getStatutesBaseClass()) );
			}
//			recurseEntries(viewModel.getEntries());
			return viewModel;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
