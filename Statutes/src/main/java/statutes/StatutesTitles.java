package statutes;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"lawCode", "shortTitle", "title", "abvrTitles"
	})
@SuppressWarnings("serial")
public class StatutesTitles implements Serializable {
	private String lawCode;
	private String shortTitle;
	private String title;
	private String[] abvrTitles;
	
	public StatutesTitles() {}
	public StatutesTitles(StatutesTitles codeTitles) {
		// shallow copy
		this.lawCode = codeTitles.lawCode;
		this.shortTitle = codeTitles.shortTitle;
		this.title = codeTitles.title;
		this.abvrTitles = codeTitles.abvrTitles;
	}
	
	public StatutesTitles(String lawCode, String shortTitle, String title, String[] abvrTitles) {
		this.lawCode = lawCode;
		this.shortTitle = shortTitle;
		this.title = title;
		this.abvrTitles = abvrTitles;
	}
	@JsonInclude
	public String getLawCode() {
		return lawCode;
	}
	public void setLawCode(String lawCode) {
		this.lawCode = lawCode;
	}
	@JsonInclude
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	@JsonInclude
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@JsonInclude
	public String[] getAbvrTitles() {
		return abvrTitles;
	}
	@JsonIgnore
	public String getAbvrTitle(int idx) {
		return abvrTitles[idx];
	}
	@JsonIgnore
	public String getAbvrTitle() {
		return abvrTitles[0];
	}
	public void setAbvrTitles(String[] abvrTitles) {
		this.abvrTitles = abvrTitles;
	}
}
