package opca.mailer;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import opca.model.User;

@SuppressWarnings("serial")
@XmlRootElement
public class EmailInformation implements Serializable {
	private String email;
	private String verifyKey;
	private String optoutKey;
	private int verifyCount;
	private String verifyHost;
	public EmailInformation(User user) {
		this.email = user.getEmail();
		this.verifyKey = user.getVerifyKey();
		this.optoutKey = user.getOptoutKey();
		this.setVerifyCount(user.getVerifyCount());
		this.verifyHost = "localhost:8080";
	}
	public EmailInformation() {
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getVerifyKey() {
		return verifyKey;
	}
	public void setVerifyKey(String verifyKey) {
		this.verifyKey = verifyKey;
	}
	public String getVerifyHost() {
		return verifyHost;
	}
	public void setVerifyHost(String verifyHost) {
		this.verifyHost = verifyHost;
	}
	public int getVerifyCount() {
		return verifyCount;
	}
	public void setVerifyCount(int verifyCount) {
		this.verifyCount = verifyCount;
	}
	public String getOptoutKey() {
		return optoutKey;
	}
	public void setOptoutKey(String optoutKey) {
		this.optoutKey = optoutKey;
	}
}
