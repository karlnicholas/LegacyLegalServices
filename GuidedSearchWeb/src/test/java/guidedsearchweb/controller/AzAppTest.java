package guidedsearchweb.controller;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import gsearch.GSearch;
import gsearch.viewmodel.SectionText;
import gsearch.viewmodel.ViewModel;

public class AzAppTest {

	Logger logger = Logger.getLogger(AzAppTest.class.getName());
	private static final String emptyString = "";

	@Test
    public void testApp() throws Exception
    {
		
// POW-0%2FPOW-1-4%2FPOW-2-4		
		ParserInterfaceRsCa parserInterface = new ParserInterfaceRsCa();
		GSearch gsearch = new GSearch(parserInterface);
    	
    	// initial state
    	ViewModel viewModel = gsearch.handleRequest(emptyString, emptyString, false);
    	printviewModel(viewModel);
    	
       	logger.fine( "Group 0" );    
        assertEquals( viewModel.getState(), ViewModel.STATES.START);
        
    	viewModel = gsearch.handleRequest(emptyString, emptyString, false );
       	printviewModel(viewModel);

       	logger.fine( "Group 1" );    
        assertEquals( ViewModel.STATES.START, viewModel.getState() );
        assertEquals( emptyString, viewModel.getPath() );
        assertEquals( emptyString, viewModel.getTerm());
        assertEquals(  47, viewModel.getEntries().size() );

    	viewModel = gsearch.handleRequest("POW-0", emptyString, false ); 
       	printviewModel(viewModel);

       	logger.fine( "Group 2" );    
        assertEquals( ViewModel.STATES.BROWSE, viewModel.getState());
        assertEquals( "POW-0", viewModel.getPath());
        assertEquals( emptyString , viewModel.getTerm());
        assertEquals( 1, viewModel.getEntries().size());
        assertEquals( 4, viewModel.getEntries().get(0).getEntries().size());
        assertEquals( 0, viewModel.getEntries().get(0).getEntries().get(0).getCount());
//        assertEquals( viewModel.subcodeList.get(0).count, 0 );
//        assertEquals( viewModel.sectionTextList.size(), 0 );

    	viewModel = gsearch.handleRequest( "POW-0/POW-1-4", emptyString, false );
       	printviewModel(viewModel);

       	logger.fine( "Group 3" );    
        assertEquals( ViewModel.STATES.BROWSE, viewModel.getState() );
        assertEquals( "POW-0/POW-1-4", viewModel.getPath());
        assertEquals( emptyString, viewModel.getTerm());
        assertEquals( 1, viewModel.getEntries().size());
        assertEquals( 1, viewModel.getEntries().get(0).getEntries().size() );
        // if this below fails its an indication that the comparison test is GSearch.processTerm() is broken
        assertEquals( 13, viewModel.getEntries().get(0).getEntries().get(0).getEntries().size());
//        assertEquals( 307, ((SectionText)viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0)).getText().length());  // was 314?

    	viewModel = gsearch.handleRequest("POW-0/POW-1-4/POW-2-4", emptyString, false );
       	printviewModel(viewModel);

       	logger.fine( "Group 4" );    
        assertEquals( ViewModel.STATES.TERMINATE, viewModel.getState() );
        assertEquals( "POW-0/POW-1-4/POW-2-4", viewModel.getPath());
        assertEquals( emptyString, viewModel.getTerm());
        assertEquals( 1, viewModel.getEntries().size());
        assertEquals( 1, viewModel.getEntries().get(0).getEntries().size() );
        // if this below fails its an indication that the comparison test is GSearch.processTerm() is broken
        assertEquals( 1, viewModel.getEntries().get(0).getEntries().get(0).getEntries().size());
        assertEquals( 307, ((SectionText)viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0)).getText().length());  // was 314?

        /*        
    	viewModel = gsearch.handleRequest(emptyString, emptyString, false );
       	printviewModel(viewModel);

       	logger.fine( "Group 5" );    
       	assertEquals( viewModel.getState(), ViewModel.STATES.START );
        assertEquals( viewModel.getPath(), emptyString );
        assertEquals( viewModel.getTerm(), emptyString );
        assertEquals( viewModel.getEntries().size(), 29 );
        
    	viewModel = gsearch.handleRequest(emptyString, "tenant", false );
       	printviewModel(viewModel);

       	logger.fine( "Group 6" );    
       	assertEquals( viewModel.getState(), ViewModel.STATES.START );
        assertEquals( viewModel.getPath(), emptyString );
        assertEquals( viewModel.getTerm(), "tenant" );
        assertEquals( viewModel.getEntries().size(), 29 );
        // these counts may well change when the actual code is updated
        assertEquals( viewModel.getEntries().get(1).getCount(), 197);

    	viewModel = gsearch.handleRequest("civ-0", "tenant", false );
       	printviewModel(viewModel);

       	logger.fine( "Group 7" );    
       	assertEquals( viewModel.getState(), ViewModel.STATES.BROWSE );
        assertEquals( viewModel.getPath(), "civ-0" );
        assertEquals( viewModel.getTerm(), "tenant" );
        assertEquals( viewModel.getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().size(), 8 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(5).getCount(), 56 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(7).getEntries().size(), 0 );

    	viewModel = gsearch.handleRequest("civ-0/civ-1-5", "tenant", false ); 
       	printviewModel(viewModel);

       	logger.fine( "Group 8" );    
        assertEquals( viewModel.getState(), ViewModel.STATES.BROWSE );
        assertEquals( viewModel.getPath(), "civ-0/civ-1-5" );
        assertEquals( viewModel.getTerm(), "tenant" );
        assertEquals( viewModel.getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().size(), 4 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(1).getCount(), 39 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(3).getEntries().size(), 0 );

    	viewModel = gsearch.handleRequest( "civ-0/civ-1-5/civ-2-1", "tenant", false );
       	printviewModel(viewModel);

       	logger.fine( "Group 9" );    
        assertEquals( viewModel.getState() , ViewModel.STATES.BROWSE );
        assertEquals( viewModel.getPath(), "civ-0/civ-1-5/civ-2-1" );
        assertEquals( viewModel.getTerm(), "tenant" );
        assertEquals( viewModel.getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 6 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(2).getCount(), 6 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(5).getEntries().size(), 0 );

    	viewModel = gsearch.handleRequest("civ-0/civ-1-5/civ-2-1/civ-3-2", "tenant", false );
       	printviewModel(viewModel);

       	logger.fine( "Group 10" );    
        assertEquals( viewModel.getState(), ViewModel.STATES.BROWSE );
        assertEquals( viewModel.getPath(), "civ-0/civ-1-5/civ-2-1/civ-3-2" );
        assertEquals( viewModel.getTerm(), "tenant" );
        assertEquals( viewModel.getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 3 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 0 );

    	viewModel = gsearch.handleRequest("civ-0/civ-1-5/civ-2-1/civ-3-2/civ-4-1", "tenant", false );
       	printviewModel(viewModel);

       	logger.fine( "Group 11" );    
        assertEquals( viewModel.getState(), ViewModel.STATES.TERMINATE );
        assertEquals( viewModel.getPath(), "civ-0/civ-1-5/civ-2-1/civ-3-2/civ-4-1" );
        assertEquals( viewModel.getTerm(), "tenant" );
        assertEquals( viewModel.getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        // if this below fails its an indication that the comparison test is GSearch.processTerm() is broken
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 11 );
        assertEquals( ((SectionText)viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(3)).getText().length(), 1839 );	// not 1868?

    	viewModel = gsearch.handleRequest("civ-0/civ-1-5/civ-2-1/civ-3-2/civ-4-1", "\"responsibility of the owner\"", false );
       	printviewModel(viewModel);

       	logger.fine( "Group 12" );    
        assertEquals( viewModel.getState(), ViewModel.STATES.TERMINATE );
        assertEquals( viewModel.getPath(), "civ-0/civ-1-5/civ-2-1/civ-3-2/civ-4-1" );
        assertEquals( viewModel.getTerm(), "\"responsibility of the owner\"" );
        assertEquals( viewModel.getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 1 );
        // if this below fails its an indication that the comparison test is GSearch.processTerm() is broken
        assertEquals( viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().size(), 11 );
        // This is testing that the "No Terms Found." is appearing.
        assertEquals( ((SectionText)viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0)).getText().length(), 20 );
        assertEquals( ((SectionText)viewModel.getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(0).getEntries().get(8)).getText().length(), 1113 );

//        gsearch.destroy();
 
 */
        
    }

    private void printviewModel(ViewModel viewModel) {
    	
    	logger.fine( "State = " + viewModel.getState() + ": Path = " + viewModel.getPath() + ": Term = " + viewModel.getTerm() + "\n" + 
			"codeList = " + viewModel.getEntries() + "\n" ); // + 
//			"sectionTextList = " + viewModel.sectionTextList + "\n" ); 
    	    	
    }
}

