package guidedsearchweb.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gsearch.GSearch;
import gsearch.viewmodel.ViewModel;

@SuppressWarnings("serial")
@WebServlet({""})
public class GuidedSearchController extends HttpServlet {
	private static final Logger logger = Logger.getLogger(GuidedSearchController.class.getName());		
	private UrlBuilder urlBuilder = new UrlBuilder();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GSearch gsearch = new GSearch(new ParserInterfaceRsCa());
		
		String path = request.getParameter("path"); 
		String term = request.getParameter("term"); 
		boolean frag = Boolean.parseBoolean( request.getParameter("frag") ); 

    	ViewModel viewModel;
		try {
			viewModel = gsearch.handleRequest(path, term, frag);
		} catch (IOException e) {			
			throw new RuntimeException(e);
		}		
//		viewModel.setTerm(StringEscapeUtils.escapeHtml4( viewModel.getTerm() ));
		setAdvancedSearchFields(request, term);
		request.setAttribute("viewModel", viewModel );
	
		// process the requests with in the MODEL
		logger.fine("1: State = " + viewModel.getState() );
		logger.fine("1: Path = " + viewModel.getPath() + ": Term = " + viewModel.getTerm() );

		request.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(request, response);
	}
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getParameter("path"); 
		String term = request.getParameter("term"); 
		boolean frag = Boolean.parseBoolean( request.getParameter("frag") ); 
		String ntm = request.getParameter("ntm"); 
		String cl = request.getParameter("cl"); 
		String to = request.getParameter("to");
		String inAll = request.getParameter("inAll"); 
		String inNot = request.getParameter("inNot");
		String inAny = request.getParameter("inAny");
		String inExact = request.getParameter("inExact"); 
    	
    	// navbar toggle fragments
		if ( to != null ) frag = !frag;
		
		// navbar copy of the (possibly new) term;
//		if ( !ntm.isEmpty() ) term = ntm;
//		term = ntm;

		// navbar clear term and fragments
		String bTerm = null;
		if ( 
			cl == null && (
			!inAll.isEmpty()
			|| !inNot.isEmpty()
			|| !inAny.isEmpty()
			|| !inExact.isEmpty()
			)
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
		if ( cl != null ) {
			term = null;
			frag = false;
		} else if ( bTerm != null && ( term == null || !bTerm.equals(term) && ntm.equals(term)) ) {
			term = bTerm;
		} else if ( !ntm.equals(term) ) {
			term = ntm;
		}
				
		response.sendRedirect("search.jsp"+urlBuilder.UrlArgs(path, term, frag));    	
    }
    
    private String appendOp(String val, char op) {
    	val = val.trim();
    	if ( val.isEmpty()) return "";
    	String[] terms = val.trim().split(" ");
    	StringBuilder sb = new StringBuilder();
    	for ( String term: terms ) {
    		sb.append(op+term+" ");
    	}
    	return sb.toString();
    }
    
    private void setAdvancedSearchFields(HttpServletRequest request, String term) {
    	if ( term == null || term.isEmpty() ) return;
    	try {
	    	String[] terms = term.split(" ");
	    	String all = new String();
	    	String not = new String();
	    	String any = new String();
	    	String exact = new String();
	    	boolean ex = false;
	    	for(String t: terms) {
	    		if ( !ex && t.startsWith("+")) all=all.concat(t.substring(1) + " ");
	    		else if ( !ex && t.startsWith("-")) not=not.concat(t.substring(1) + " " );
	    		else if ( !ex && (t.startsWith("\"") && t.trim().endsWith("\"")) ) {
	    			exact=exact.concat(t.substring(1, t.length()-1) + " ");
	    		}
	    		else if ( !ex && t.startsWith("\"")) {
	    			exact=exact.concat(t.substring(1) + " ");
	    			ex = true;
	    		}
	    		else if ( ex && !t.endsWith("\"") ) {
	    			exact=exact.concat(t) + " ";
	    		}
	    		else if ( ex && t.endsWith("\"")) {
	    			exact=exact.concat(t.substring(0, t.length()-1)) + " ";
	    			ex = false;
	    		}
	    		else any = any.concat(t) + " ";
	    	}
	    	if ( !all.isEmpty() ) request.setAttribute("inAll", all.trim());
	    	if ( !not.isEmpty() ) request.setAttribute("inNot", not.trim());
	    	if ( !any.isEmpty() ) request.setAttribute("inAny", any.trim());
	    	if ( !exact.isEmpty() ) request.setAttribute("inExact", exact.trim());
    	} catch ( Throwable t) {
    		// silent exception
    		logger.warning("Exception:" + t.getMessage());
    	}
    }

}

