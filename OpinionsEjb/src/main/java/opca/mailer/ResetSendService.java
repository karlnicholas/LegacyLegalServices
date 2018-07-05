package opca.mailer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Logger;

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

import opca.model.User;

public class ResetSendService {
	@Inject private TransformerFactory tf;
	@Inject private Logger logger;
	@Resource(mappedName = "java:jboss/mail/SendGrid")
	private Session mailSession;
	
	public void reset(User user) {
		// So, do the real work.
/*		
		RESPONSES response = sendEmail.sendEmail(
			"no-reply@op-cacode.rhcloud.com",
			account.getEmail(), 
			"Court Opinions - Reset ",
			htmlContent
		);
*/		
		try {
			JAXBContext jc = JAXBContext.newInstance(User.class);
			// jaxbContext is a JAXBContext object from which 'o' is created.
			JAXBSource source = new JAXBSource(jc, user);
			// set up XSLT transformation
			InputStream is = getClass().getResourceAsStream("/xsl/reset.xsl");
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
			message.setFrom(new InternetAddress("no-reply@op-cacode.rhcloud.com"));
			message.setSubject("Court Opinions - Reset");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			// This is not mandatory, however, it is a good
			// practice to indicate the software which
			// constructed the message.
			message.setHeader("X-Mailer", "Court Opinions");

			// Adjust the date of sending the message
			message.setSentDate(new Date());

			// Sends the email
			Transport.send(message);
			
		} catch (IOException | JAXBException | MessagingException e) {
			logger.severe(e.getMessage());
		}

//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
		logger.info("Reset sent: " + user.getEmail());
	}
		
}