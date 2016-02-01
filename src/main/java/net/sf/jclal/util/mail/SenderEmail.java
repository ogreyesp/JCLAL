/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sf.jclal.util.mail;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import net.sf.jclal.core.IConfigure;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * Utility class to send email, this class must be associated with a algorithm's
 * listener.
 *
 * @author Oscar Reyes Pupo
 * @author Eduardo Perez Perdomo
 */
public class SenderEmail implements IConfigure {

	private static final long serialVersionUID = -7854486779567117244L;

	/**
	 * Empty constructor
	 */
	public SenderEmail() {

		toRecipients = new StringBuilder();
	}

	/**
	 * Recipient's email ID needs to be mentioned
	 */
	private StringBuilder toRecipients;

	/**
	 * Sender's email ID needs to be mentioned
	 */
	private String from;

	/**
	 * The host since you are sending the email
	 */
	private String host;

	/**
	 * The user to send the message
	 */
	private String user;

	/**
	 * The password of the user
	 */
	private String pass;

	/**
	 * The smtp port
	 */
	private int port;

	/**
	 * If includes a report file
	 */
	private boolean attachReporFile;

	/**
	 * The smtp port
	 * 
	 * @return the port used.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * The smtp port
	 * 
	 * @param port
	 *            the port to use.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * The password of the user
	 * 
	 * @return The password
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * The password of the user
	 * 
	 * @param pass
	 *            The password
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * The user to send the message
	 * 
	 * @return the user name.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * The user to send the message
	 * 
	 * @param user
	 *            the user name
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * If includes a report file
	 * 
	 * @return Whether an file is attached
	 */
	public boolean isAttachReporFile() {
		return attachReporFile;
	}

	/**
	 * If it includes a report file
	 * 
	 * @param attachReporFile
	 *            Flag that indicates whether a file is attached or not.
	 */
	public void setAttachReporFile(boolean attachReporFile) {
		this.attachReporFile = attachReporFile;
	}

	/**
	 * Send the email with the indicated parameters
	 *
	 * @param subject
	 *            The subject
	 * @param content
	 *            The content
	 * @param reportFile
	 *            The reportFile to send
	 */
	public void sendEmail(String subject, String content, File reportFile) {

		// Get system properties
		Properties properties = new Properties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.debug", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.port", port);
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");

		if (!user.isEmpty() && !pass.isEmpty()) {

			properties.setProperty("mail.smtp.user", user);

			properties.setProperty("mail.password", pass);
		}

		// Get the default Session object.
		SMTPAuthenticator auth = new SMTPAuthenticator();
		Session session = Session.getInstance(properties, auth);
		session.setDebug(true);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipients(Message.RecipientType.TO, toRecipients.toString());

			// Set Subject: header field
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Fill the message
			messageBodyPart.setText(content);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			if (attachReporFile) {

				messageBodyPart = new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler(new FileDataSource(reportFile)));
				messageBodyPart.setFileName(reportFile.getName());
				multipart.addBodyPart(messageBodyPart);
			}

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {

			Logger.getLogger(SenderEmail.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	/**
	 * Recipient's email ID needs to be mentioned
	 * 
	 * @return The recipient strings
	 */
	public String getToRecipients() {
		return toRecipients.toString();
	}

	/**
	 * Recipient's email ID needs to be mentioned
	 * 
	 * @param to
	 *            to
	 */
	public void setToRecipients(String to) {
		this.toRecipients = new StringBuilder(to);
	}

	/**
	 * Sender's email ID needs to be mentioned
	 * 
	 * @return from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Sender's email ID needs to be mentioned
	 * 
	 * @param from
	 *            from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * The host since you are sending the email
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * The host since you are sending the email
	 * 
	 * @param host
	 *            the host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param configuration
	 *            The configuration of SenderEmail.
	 *
	 *            The XML labels supported are:
	 *            <ul>
	 *            <li>smtp-host= ip</li>
	 *            <li>smtp-port= int</li>
	 *            <li>to= email</li>
	 *            <li>from= email</li>
	 *            <li>attach-report-file=boolean</li>
	 *            <li>user=String</li>
	 *            <li>pass=String</li>
	 *            </ul>
	 */
	@Override
	public void configure(Configuration configuration) {

		String hostT = configuration.getString("smtp-host", "");
		if (hostT.isEmpty()) {
			throw new ConfigurationRuntimeException("\nThe tag <smtp-host></smtp-host> is empty.");
		}

		setHost(hostT);

		int portT = configuration.getInt("smtp-port", 21);

		setPort(portT);

		String fromT = configuration.getString("from", "");

		if (fromT.isEmpty()) {
			throw new ConfigurationRuntimeException("\nThe tag <from></from> is empty. ");
		}

		setFrom(fromT);

		// Number of defined recipients
		int numberRecipients = configuration.getList("to").size();

		if (numberRecipients == 0) {
			throw new ConfigurationRuntimeException("\nAt least one <to></to> tag must be defined. ");
		}

		// For each recipients in list
		for (int i = 0; i < numberRecipients; i++) {

			String header = "to(" + i + ")";

			// recipient
			String recipientName = configuration.getString(header, "");

			// Add this recipient
			toRecipients.append(recipientName).append(";");
		}

		toRecipients.deleteCharAt(toRecipients.length() - 1);

		boolean attach = configuration.getBoolean("attach-report-file", false);

		setAttachReporFile(attach);

		String userT = configuration.getString("user", "");

		if (userT.isEmpty()) {
			throw new ConfigurationRuntimeException("\nThe tag <user></user> is empty. ");
		}

		setUser(userT);

		String passT = configuration.getString("pass", "");

		if (passT.isEmpty()) {
			throw new ConfigurationRuntimeException("\nThe tag <pass></pass> is empty. ");
		}

		setPass(passT);
	}
	
	class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() { 
			
			String username = user; String password = pass; return new PasswordAuthentication(username, password); }
	}
}
