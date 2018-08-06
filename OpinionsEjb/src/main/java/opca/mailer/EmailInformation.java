package opca.mailer;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import opca.model.User;
import opca.view.OpinionView;

@SuppressWarnings("serial")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailInformation implements Serializable {
	private String email;
	private String verifyKey;
	private int verifyCount;
	private String verifyHost;
	private String comment;
	private Locale locale;
	private List<OpinionView> opinionCases;
	public EmailInformation(User user) {
		this();
		this.email = user.getEmail();
		this.verifyKey = user.getVerifyKey();
		this.verifyCount = user.getVerifyCount();
	}
	public EmailInformation(String email, String comment, Locale locale) {
		this();
		this.email = email;
		this.comment = comment;
		this.locale = locale;
	}
	public EmailInformation(User user, List<OpinionView> opinionCases) {
		this();
		this.email = user.getEmail();
		this.opinionCases = opinionCases;
	}
	public EmailInformation() {
		this.verifyHost = "localhost:8080";
	}
	public String getEmail() {
		return email;
	}
	public String getVerifyKey() {
		return verifyKey;
	}
	public String getVerifyHost() {
		return verifyHost;
	}
	public int getVerifyCount() {
		return verifyCount;
	}
	public String getComment() {
		return comment;
	}
	public Locale getLocale() {
		return locale;
	}
	public List<OpinionView> getOpinionCases() {
		return opinionCases;
	}
}