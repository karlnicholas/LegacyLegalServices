package guidedsearch.component;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

@FacesComponent("SearchTerms")
public class SearchTerms extends UIInput {
	private Logger logger = Logger.getLogger(SearchTerms.class.getName());
	private String all;
	private String not;
	private String any;
	private String exact;
	private String term;

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		if ( term == null ) {
			term = (String)getValue();
		}
	    setAdvancedSearchFields(context, term);
	    ResponseWriter writer = context.getResponseWriter();
	    // <input type="hidden" name="hiddenTerm" value="#{guidedSearchController.term}" />
	    writer.startElement("input", this);
	    writer.writeAttribute("type", "hidden", null);
	    writer.writeAttribute("name", "hiddenTerm", null);
	    writer.writeAttribute("value", term, null);
	    writer.endElement("input");
	    // <h:inputText id="searchInputText" value="#{guidedSearchController.term}" styleClass="form-control" p:placeholder="Search" />
	    writer.startElement("input", this);
	    writer.writeAttribute("id", "searchInputText", null);
	    writer.writeAttribute("class", "form-control", null);
	    writer.writeAttribute("placeholder", "Search", null);
	    writer.writeAttribute("type", "text", null);
	    writer.writeAttribute("name", "term", null);
	    writer.writeAttribute("value", term, null);
	    writer.endElement("input");
	    // <div class="btn-group dropdown">
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "btn-group dropdown", null);
	    // <button type="submit" class="btn btn-default">Submit</button>
	    writer.startElement("button", this);
	    writer.writeAttribute("class", "btn btn-default", null);
	    writer.writeAttribute("type", "submit", null);
	    writer.write("Submit");
	    writer.endElement("button");
	    // <button class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
	    writer.startElement("button", this);
	    writer.writeAttribute("class", "btn btn-default dropdown-toggle", null);
	    writer.writeAttribute("data-toggle", "dropdown", null);
	    writer.startElement("span", this);
	    writer.writeAttribute("class", "caret", null);
	    writer.endElement("span");
	    writer.endElement("button");
	    // <div class="dropdown-menu container" style="width: 350px; padding: 15px">
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "dropdown-menu container", null);
	    writer.writeAttribute("style", "width: 350px; padding: 15px", null);

	    // <div class="row">
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "row", null);
	    // <label for="inAll" class="control-label col-sm-4">All&#160;Of:&#160;&#160;</label>
	    writer.startElement("label", this);
	    writer.writeAttribute("class", "control-label col-sm-4", null);
	    writer.writeAttribute("for", "inAll", null);
	    writer.write("All Of:  ");
	    writer.endElement("label");
	    // <div class="col-sm-4"><input type="text" class="form-control" name="inAll" value="" id="inAll" /></div>
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "col-sm-4", null);
	    writer.startElement("input", this);
	    writer.writeAttribute("class", "form-control", null);
	    writer.writeAttribute("type", "text", null);
	    writer.writeAttribute("name", "inAll", null);
	    writer.writeAttribute("value", all, null);
	    writer.writeAttribute("id", "inAll", null);
	    writer.endElement("input");
	    writer.endElement("div");
	    // </div>
	    writer.endElement("div");

	    // <div class="row">
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "row", null);
	    // <label for="inNot" class="control-label col-sm-4">None&#160;Of:&#160;&#160;</label>
	    writer.startElement("label", this);
	    writer.writeAttribute("class", "control-label col-sm-4", null);
	    writer.writeAttribute("for", "inNot", null);
	    writer.write("None Of:  ");
	    writer.endElement("label");
	    // <div class="col-sm-4"><input type="text" class="form-control" name="inNot" value="" id="inNot" /></div>
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "col-sm-4", null);
	    writer.startElement("input", this);
	    writer.writeAttribute("class", "form-control", null);
	    writer.writeAttribute("type", "text", null);
	    writer.writeAttribute("name", "inNot", null);
	    writer.writeAttribute("value", not, null);
	    writer.writeAttribute("id", "inNot", null);
	    writer.endElement("input");
	    writer.endElement("div");	    
	    // </div>
	    writer.endElement("div");
	    
	    // <div class="row">
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "row", null);
	    // <label for="inAny" class="control-label col-sm-4">Any&#160;Of:&#160;&#160;</label>
	    writer.startElement("label", this);
	    writer.writeAttribute("class", "control-label col-sm-4", null);
	    writer.writeAttribute("for", "inAny", null);
	    writer.write("Any Of:  ");
	    writer.endElement("label");
	    // <div class="col-sm-4"><input type="text" class="form-control" name="inAny" value="" id="inAny" /></div>
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "col-sm-4", null);
	    writer.startElement("input", this);
	    writer.writeAttribute("class", "form-control", null);
	    writer.writeAttribute("type", "text", null);
	    writer.writeAttribute("name", "inAny", null);
	    writer.writeAttribute("value", any, null);
	    writer.writeAttribute("id", "inAny", null);
	    writer.endElement("input");
	    writer.endElement("div");	    
	    // </div>
	    writer.endElement("div");

	    // <div class="row">
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "row", null);
	    // <label for="inExact" class="control-label col-sm-4">Exact&#160;Phrase:&#160;&#160;</label>
	    writer.startElement("label", this);
	    writer.writeAttribute("class", "control-label col-sm-4", null);
	    writer.writeAttribute("for", "inExact", null);
	    writer.write("Exact Phrase:  ");
	    writer.endElement("label");
	    // <div class="col-sm-4"><input type="text" class="form-control" name="inExact" value="" id="inExact" /></div>
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "col-sm-4", null);
	    writer.startElement("input", this);
	    writer.writeAttribute("class", "form-control", null);
	    writer.writeAttribute("type", "text", null);
	    writer.writeAttribute("name", "inExact", null);
	    writer.writeAttribute("value", exact, null);
	    writer.writeAttribute("id", "inExact", null);
	    writer.endElement("input");
	    writer.endElement("div");	    
	    // </div>
	    writer.endElement("div");

	    // <div class="row">
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "row", null);
	    // <label for="submit" class="control-label col-sm-4"></label>
	    writer.startElement("label", this);
	    writer.writeAttribute("class", "control-label col-sm-4", null);
	    writer.writeAttribute("for", "submit", null);
	    writer.endElement("label");
	    // <div class="col-sm-4"><button type="submit" class="form-control" id="submit">Submit</button></div>
	    writer.startElement("div", this);
	    writer.writeAttribute("class", "col-sm-4", null);
	    writer.startElement("button", this);
	    writer.writeAttribute("class", "form-control", null);
	    writer.writeAttribute("type", "submit", null);
	    writer.writeAttribute("id", "submit", null);
	    writer.write("Submit");
	    writer.endElement("button");
	    writer.endElement("div");	    
	    // </div>
	    writer.endElement("div");

	    // </div>
	    writer.endElement("div");
	    // </div>
	    writer.endElement("div");
    }
		
	@Override
	public void decode(FacesContext context) {
		/* Grab the request map from the external context */
		Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
		/* Get client ID, use client ID to grab value from parameters */
		String newTerm = requestParameterMap.get("term");
		String hiddenTerm = requestParameterMap.get("hiddenTerm");
		// navbar clear term and fragments
		String inAll = requestParameterMap.get("inAll") == null ? "" : requestParameterMap.get("inAll");
		String inNot = requestParameterMap.get("inNot") == null ? "" : requestParameterMap.get("inNot");
		String inAny = requestParameterMap.get("inAny") == null ? "" : requestParameterMap.get("inAny");
		String inExact = requestParameterMap.get("inExact") == null ? "" : requestParameterMap.get("inExact");

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
		} else */ if ( bTerm != null && ( hiddenTerm == null || !bTerm.equals(hiddenTerm) && newTerm.equals(hiddenTerm)) ) {
			term = bTerm;
		} else if ( !newTerm.equals(hiddenTerm) ) {
			term = newTerm;
		}
		setValue(term);
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
    
    private void setAdvancedSearchFields(FacesContext context, String term) {
    	if ( term == null || term.isEmpty() ) return;
    	try {
	    	String[] terms = term.split(" ");
	    	all = new String();
	    	not = new String();
	    	any = new String();
	    	exact = new String();
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
	    	all = all.trim();
	    	any = any.trim();
	    	not = not.trim();
	    	exact = exact.trim();
    	} catch ( Throwable t) {
    		// silent exception
    		logger.warning("Exception:" + t.getMessage());
    	}
    }
}

