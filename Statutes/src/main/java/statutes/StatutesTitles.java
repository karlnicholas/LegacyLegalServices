package statutes;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"lawCode", "shortTitle", "commonTitle", "fullTitle", "abvrTitles"
	})
@SuppressWarnings("serial")
public class StatutesTitles implements Serializable {
	private String lawCode;
	private String shortTitle;
	private String commonTitle;
	private String fullTitle;
	private String[] abvrTitles;
	
	public StatutesTitles() {}
	public StatutesTitles(StatutesTitles codeTitles) {
		// shallow copy
		this.lawCode = codeTitles.lawCode;
		this.shortTitle = codeTitles.shortTitle;
		this.commonTitle = codeTitles.commonTitle;
		this.fullTitle = codeTitles.fullTitle;
		this.abvrTitles = codeTitles.abvrTitles;
	}
	
	public StatutesTitles(String lawCode, String shortTitle, String commonTitle, String fullTitle, String[] abvrTitles) {
		this.lawCode = lawCode;
		this.shortTitle = shortTitle;
		this.commonTitle = commonTitle;
		this.fullTitle = fullTitle;
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
	public String getCommonTitle() {
		return commonTitle;
	}
	public void setCommonTitle(String commonTitle) {
		this.commonTitle = commonTitle;
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
