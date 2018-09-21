package guidedsearchweb.controller;

import gsearch.viewmodel.ViewModel;

import java.io.UnsupportedEncodingException;
import java.net.*;

public class UrlBuilder {
	public String newPathUrl(ViewModel viewModel, String newPath) throws UnsupportedEncodingException {
		return UrlArgs( newPath, viewModel.getTerm(), viewModel.isFragments() );
	}
	
	public String UrlArgs(String path, String term, boolean frag) throws UnsupportedEncodingException {
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

	public String homeUrl(ViewModel viewModel) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder("/");
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
}