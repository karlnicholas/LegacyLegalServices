package opca.mailer;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import opca.model.User;
import opca.view.OpinionView;

public class SendGridMailer {
	@Inject private TransformerFactory tf;
	@Inject private Logger logger;
	@Resource(mappedName = "java:jboss/mail/SendGrid")
	private Session mailSession;

	public boolean sendComment(String email, String comment, Locale locale) {
		return sendGridEmail(new EmailInformation(email, comment, locale), "/xsl/about.xsl");
	}
	
	public boolean sendEmail(User user, String emailResource) {
		return sendGridEmail(new EmailInformation(user), emailResource);
	}

	public boolean sendOpinionReport(User user, List<OpinionView> opinionCases) {
		return sendGridEmail(new EmailInformation(user, opinionCases), "/xsl/opinionreport.xsl");
	}
	public boolean sendGridEmail(EmailInformation emailInformation, String emailResource) {
		
		try {
			JAXBContext jc = JAXBContext.newInstance(EmailInformation.class);
			JAXBSource source = new JAXBSource(jc, emailInformation);
			// set up XSLT transformation
			InputStream is = getClass().getResourceAsStream(emailResource);
			StreamSource streamSource = new StreamSource(is);
			StringWriter htmlContent = null;
			try {
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
			message.setFrom(new InternetAddress("no-reply@op-opca.b9ad.pro-us-east-1.openshiftapps.com"));
			message.setSubject("Welcome to Court Opinions");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailInformation.getEmail()));
			// This is not mandatory, however, it is a good
			// practice to indicate the software which
			// constructed the message.
			message.setHeader("X-Mailer", "Court Opinions");

			// Adjust the date of sending the message
			message.setSentDate(new Date());

			// Sends the email
			Transport.send(message);
			
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendGridPrint(EmailInformation emailInformation, String emailResource) {
		
		try {
			JAXBContext jc = JAXBContext.newInstance(EmailInformation.class);
			JAXBSource source = new JAXBSource(jc, emailInformation);
			// set up XSLT transformation
			InputStream is = getClass().getResourceAsStream(emailResource);
			StreamSource streamSource = new StreamSource(is);
			StringWriter htmlContent = null;
			try {
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

			System.out.println(  htmlContent );
			
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return false;
		}
		return true;
	}
}