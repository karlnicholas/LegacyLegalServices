package opca.parser;

import java.util.ArrayList;
import java.util.List;

import opca.model.OpinionBase;

public class ScrapedOpinionDocument {
	private List<String> paragraphs; 
	private List<String> footnotes;
	private final OpinionBase opinionBase;
	
	public ScrapedOpinionDocument(OpinionBase opinionBase) {
		this.opinionBase = opinionBase;
		paragraphs = new ArrayList<String>(); 
		footnotes = new ArrayList<String>();
	}

	public List<String> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<String> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public List<String> getFootnotes() {
		return footnotes;
	}

	public void setFootnotes(List<String> footnotes) {
		this.footnotes = footnotes;
	}

	public OpinionBase getOpinionBase() {
		return opinionBase;
	}
	
}
