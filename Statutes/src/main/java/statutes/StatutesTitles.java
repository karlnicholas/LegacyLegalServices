package statutes;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"facetHead", "shortTitle", "fullTitle", "abvrTitles"
	})
@SuppressWarnings("serial")
public class StatutesTitles implements Serializable {
	private String facetHead;
	private String shortTitle;
	private String fullTitle;
	private String[] abvrTitles;
	
	public StatutesTitles() {}
	public StatutesTitles(StatutesTitles codeTitles) {
		// shallow copy
		this.facetHead = codeTitles.facetHead;
		this.shortTitle = codeTitles.shortTitle;
		this.fullTitle = codeTitles.fullTitle;
		this.abvrTitles = codeTitles.abvrTitles;
	}
	
	public StatutesTitles(String facetHead, String shortTitle, String fullTitle, String[] abvrTitles) {
		this.facetHead = facetHead;
		this.shortTitle = shortTitle;
		this.fullTitle = fullTitle;
		this.abvrTitles = abvrTitles;
	}
	@JsonInclude
	public String getFacetHead() {
		return facetHead;
	}
	public void setFacetHead(String facetHead) {
		this.facetHead = facetHead;
	}
	@JsonInclude
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	@JsonInclude
	public String getFullTitle() {
		return fullTitle;
	}
	public void setFullTitle(String fullTitle) {
		this.fullTitle = fullTitle;
	}
	@JsonInclude
	public String[] getAbvrTitles() {
		return abvrTitles;
	}
	public String getAbvrTitle(int idx) {
		return abvrTitles[idx];
	}
	public String getAbvrTitle() {
		return abvrTitles[0];
	}
	public void setAbvrTitles(String[] abvrTitles) {
		this.abvrTitles = abvrTitles;
	}
}
