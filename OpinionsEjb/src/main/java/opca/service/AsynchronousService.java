package opca.service;

import java.util.Locale;

import javax.ejb.Asynchronous;
import javax.inject.Inject;

import opca.model.User;
import opca.mailer.AboutMailer;
import opca.mailer.VerifyMailer;

@Asynchronous
public class AsynchronousService {
	@Inject VerifyMailer verifyMailer;
	@Inject AboutMailer aboutMailer;
	
	public void sendVerifyEmail(User user) {
		verifyMailer.sendEmail(user);
	}
	
	public void sendAboutMail(String email, String comment, Locale locale) {
		aboutMailer.send(email, comment, locale);
	}

}
