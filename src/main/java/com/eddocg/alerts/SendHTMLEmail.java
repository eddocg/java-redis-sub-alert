package com.eddocg.alerts;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SendHTMLEmail {
	
	public static final String HTML = "text/html";
	public static final String WORKERNAME = "workerName";
	public static final String CONFIG_FILE = "config.properties";
	public static final String CLASSPATH = "classpath";
	public static final String CLASSPATH_CLASS = "classpath.resource.loader.class";
	public static final String MAIL_TEMPLATE ="mail.template";
	public static final String MAIL_SUBJECT ="mail.subject";
	public static final String MAIL_USERNAME ="mail.username";
	public static final String MAIL_PASS ="mail.password";
	public static final String MAIL_FROM ="mail.from";
	
	static final Logger logger = LoggerFactory.getLogger(SendHTMLEmail.class);
	
	private Worker getWorkerFromJson(String msg) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		//Convert JSON String from Message to Worker
		return mapper.readValue(msg, Worker.class);
	}
	
	private Properties getConfigProperties() throws IOException {
		Properties props = new Properties();
		InputStream resourceStream = SendHTMLEmail.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
		props.load(resourceStream);
		return props;
	}
	
	private VelocityEngine initVelocityEngine() {
		/*first get and initialize an engine*/
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, CLASSPATH);
		ve.setProperty(CLASSPATH_CLASS, ClasspathResourceLoader.class.getName());
		ve.init();
		return ve;
	}
	
	private VelocityContext initVelocityContext(Worker worker) {
		//Create new context object
		VelocityContext context = new VelocityContext();
		//Adding the workerName to the context
		context.put(WORKERNAME, worker.getWorkerName());
		/*Here we can add more variables to the context
		 *  that will be use for the template*/
		return context;
	}
	
	public void sendMail(String jsonMsg) {
		logger.debug("Starting sendMail()");
		String to = "";
		try {
			Worker worker = getWorkerFromJson(jsonMsg);
			Properties props =  getConfigProperties();
			
			//Recipient's email ID needs to be mentioned.
			to = worker.getEmail();
			logger.debug("Trying to send email to:{}", to);
			
			//Sender's email ID needs to be mentioned.
			final String from = props.getProperty(MAIL_FROM);
			final String username = props.getProperty(MAIL_USERNAME);
			final String password = props.getProperty(MAIL_PASS);
			
			//Get the Session object
			javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
			
			logger.debug("Session started successfully in Email Server");
			
			/*first, get an initialize an engine*/
			VelocityEngine ve =initVelocityEngine();
			
			/*create the context and add more data*/
			VelocityContext context = initVelocityContext(worker);
			
			/*next get the Template*/
			Template t = ve.getTemplate(props.getProperty(MAIL_TEMPLATE));
			
			/*create a new stringWriter*/
			StringWriter writer = new StringWriter();
			
			/*now render the template into a StringWriter*/
			t.merge(context, writer);
			
			/*create a default MimeMessage object*/
			Message message = new MimeMessage(session);
			
			/*set from: header field of the header*/
			message.setFrom(new InternetAddress(from));
			
			/*set to: header field of the header*/
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			
			/*Set subject*/
			message.setSubject(props.getProperty(MAIL_SUBJECT));
			
			/*Send the velocity Template as the email*/
			message.setContent(writer.toString(),HTML);
			
			/*Send email*/
			Transport.send(message);
			
			logger.debug("Email sent successfully to:{}",to);
			logger.debug("End sendMail()");
		} catch (MessagingException | IOException e) {
			logger.error("Error when trying to send email to:{}",to);
			logger.error("The details of the error is:{}", e);
		}//End of catch
	}//End of sendMail

}//End of class