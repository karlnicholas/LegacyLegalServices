package opca.mailer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import opca.model.User;
import opca.service.UserService;

public class VerifyMailer {
	@Inject private TransformerFactory tf;
	@Inject private UserService userService;
	@Inject private Logger logger;
	@Resource(mappedName = "java:jboss/mail/SendGrid")
	private Session mailSession;

	public void sendEmail(User user) {
		// too many errors?
		if ( user.getVerifyErrors() > 3 ) return;
		try {
			JAXBContext jc = JAXBContext.newInstance(VerifyInformation.class);
			// jaxbContext is a JAXBContext object from which 'o' is created.
			JAXBSource source = new JAXBSource(jc, new VerifyInformation(user));
			// set up XSLT transformation
			StringWriter htmlContent = null;
			try {
				StreamSource streamSource = new StreamSource(
					getClass().getResourceAsStream("/xsl/verify.xsl")
				);
				htmlContent = new StringWriter();
				synchronized(this) {
					Transformer t = tf.newTransformer(streamSource);
					// run transformation
					t.transform(source, new StreamResult(htmlContent));
				}
			} catch (TransformerException e) {
				throw new RuntimeException(e); 
			} finally {
				htmlContent.close();
			}

			MimeMessage message = new MimeMessage(mailSession);

			Multipart multiPart = new MimeMultipart("alternative");

			// Sets up the contents of the email message
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText("");

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlContent.toString(), "text/html; charset=utf-8");

			multiPart.addBodyPart(textPart); // <-- first
			multiPart.addBodyPart(htmlPart); // <-- second

			message.setContent(multiPart);
			message.setFrom(new InternetAddress("no-reply@op-jsec.rhcloud.com"));
			message.setSubject("Court Opinions - Please Verify Your Account");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			// This is not mandatory, however, it is a good
			// practice to indicate the software which
			// constructed the message.
			message.setHeader("X-Mailer", "Court Opinions");

			// Adjust the date of sending the message
			message.setSentDate(new Date());

			// Sends the email
			Transport.send(message);
			
			userService.incrementVerifyCount(user);
			
		} catch (SendFailedException e) {
			userService.incrementVerifyErrors(user);
		} catch (IOException | JAXBException | MessagingException e) {
			userService.incrementVerifyErrors(user);
			logger.severe(e.getMessage());
		}

//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
//		log.info("Feedback sent: " + response);
	}
}
/*
public void verify(Account account) {
	// So, do the real work.
	// / accountRepository.findUnverified

	sendEmail(account);
	
//	String htmlContent = mailTemplateEngine.process("verify.html", ctx);
	log.info("VerifyEmail sent: " );
}
	
@Scheduled(cron="0 30 0 * * ?")		// 12:30 am every day
public void verifyHousekeeping() {
	// So, do the real work.
	// / accountRepository.findUnverified
	Calendar cal = Calendar.getInstance();
	int year = cal.get(Calendar.YEAR);
	int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
	dayOfYear = dayOfYear - 4;
	if ( dayOfYear < 1 ) {
		year = year - 1;
		dayOfYear = 365 + dayOfYear;
	}
	cal.set(Calendar.YEAR, year);
	cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
	Date threeDaysAgo = cal.getTime();

	List<Account> accounts = accountRepository.findAllUnverified();
	for ( Account account: accounts ) {
		if ( account.getCreateDate().compareTo(threeDaysAgo) < 0 ) {
			accountRepository.delete(account.getEmail());
			continue;
		}

		// Prepare the evaluation context
		sendEmail(account);
//		System.out.println("Resend = " + account.getEmail());
	}
		
//	String htmlContent = mailTemplateEngine.process("verify.html", ctx);
	log.info("VerifyEmail sent: " );
}

private void sendEmail(Account account) {
	// too many errors?
	if ( account.getVerifyErrors() > 3 ) return;
	
	Context ctx = new Context(account.getLocale());
	// Prepare the evaluation context
	ctx.setVariable("account", account);
	String htmlContent = mailTemplateEngine.process("verify.html", ctx);
	RESPONSES response = sendEmail.sendEmail(
		"no-reply@op-cacode.rhcloud.com",
		account.getEmail(), 
		"Court Opinions - Please Verify Your Account",
		htmlContent
	);

	if ( response != RESPONSES.OK ) {
		account.setVerifyErrors(account.getVerifyErrors()+1);
		accountRepository.merge(account);
	} else {
		account.setVerifyCount(account.getVerifyCount()+1);
		accountRepository.merge(account);
	}
	
}
*/
