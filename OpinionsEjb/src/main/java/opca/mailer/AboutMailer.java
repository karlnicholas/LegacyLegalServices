package opca.mailer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
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

public class AboutMailer {

	@Inject private TransformerFactory tf;
	@Resource(mappedName = "java:jboss/mail/Default")
	private Session mailSession;
	
	private String email;
	private String comment;	

	public void send(String email, String comment, Locale locale) {
		
		try {
			JAXBContext jc = JAXBContext.newInstance(AboutMailer.class);
			// jaxbContext is a JAXBContext object from which 'o' is created.
			JAXBSource source = new JAXBSource(jc, this);
			// set up XSLT transformation
			InputStream is = getClass().getResourceAsStream("/xsl/about.xsl");
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
/*			
			sendEmail.sendEmail(
				"no-reply@op-cacode.rhcloud.com",
				"karl.nicholas@outlook.com", 
				"Court Opinions - Feedback",
				htmlContent.toString()
			);
*/
			MimeMessage message = new MimeMessage(mailSession);

			Multipart multiPart = new MimeMultipart("alternative");

			// Sets up the contents of the email message
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(
					"From: " + email
					+ "Comment: " + comment, "utf-8");

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlContent.toString(), "text/html; charset=utf-8");

			multiPart.addBodyPart(textPart); // <-- first
			multiPart.addBodyPart(htmlPart); // <-- second

			message.setContent(multiPart);
			message.setFrom(new InternetAddress(email));
			message.setSubject("Comment from user");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("karl.nicholas@outlook.com"));
			// This is not mandatory, however, it is a good
			// practice to indicate the software which
			// constructed the message.
			message.setHeader("X-Mailer", "Court Opinions");

			// Adjust the date of sending the message
			message.setSentDate(new Date());

			// Sends the email
			Transport.send(message);
			
		} catch (IOException | JAXBException | MessagingException e) {
			new RuntimeException(e);
		}

//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
//		log.info("Feedback sent: " + response);
	}

	public String getComment() {
		return comment;
	}

	public String getEmail() {
		return email;
	}
}