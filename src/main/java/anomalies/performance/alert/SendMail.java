/**
 * 
 */
package anomalies.performance.alert;

/**
 * @author statchum
 *
 */
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class SendMail {

	private String from;
	private String to;
	private String subject;
	private String text;

	public SendMail() {
	}

	public static void send(final String from, final String to, final String subject, final String text) {

		final Properties props = new Properties();
		props.put("mail.smtp.host", "localdelivery.klee.lan.net");

		final Session mailSession = Session.getDefaultInstance(props);
		final Message simpleMessage = new MimeMessage(mailSession);

		InternetAddress fromAddress = null;
		InternetAddress toAddress = null;
		try {
			fromAddress = new InternetAddress(from);
			toAddress = new InternetAddress(to);
		} catch (final AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			simpleMessage.setFrom(fromAddress);
			simpleMessage.setRecipient(RecipientType.TO, toAddress);
			simpleMessage.setSubject(subject);
			simpleMessage.setText(text);

			Transport.send(simpleMessage);
		} catch (final MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
