package guidedsearch.restapi;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import api.gsearch.GSearch;
import api.gsearch.viewmodel.EntryReference;
import api.gsearch.viewmodel.ViewModel;
import statutes.StatutesBaseClass;
import statutes.StatutesLeaf;
import statutes.StatutesNode;
import statutes.StatutesRoot;

@Path("gs")
@Produces("application/json")
public class GuidedSearchApi {
	private GSearch gsearch;

    public GuidedSearchApi() {
		try {
			String gsindexloc = System.getenv("gsindexloc");
			if ( gsindexloc == null ) {
				gsindexloc = "c:/users/karln/opcastorage/index";
			}

			String gsindextaxoloc = System.getenv("gsindextaxoloc");
			if ( gsindextaxoloc == null ) {
				gsindextaxoloc = "c:/users/karln/opcastorage/indextaxo";
			}

			ParserInterfaceRsCa parserInterface = new ParserInterfaceRsCa();
			gsearch = new GSearch(parserInterface, Paths.get(gsindexloc), Paths.get(gsindextaxoloc));

		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
		
	}

    @GET
	public ViewModel handleRequest( @QueryParam("path") String path, @QueryParam("term") String term, @QueryParam("fragments") boolean fragments) {
		// yea, this is all wrong .. 
		try {
			ViewModel viewModel = gsearch.handleRequest(path, term, fragments);
			if ( viewModel.getStatutesBaseClass() != null ) {
				viewModel.setStatutesBaseClass( createNewBaseClass(viewModel.getStatutesBaseClass()) );
			}
			recurseEntries(viewModel.getEntries());
			return viewModel;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
