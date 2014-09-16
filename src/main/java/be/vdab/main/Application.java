package be.vdab.main;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mail.ImapIdleChannelAdapter;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import be.vdab.entities.Email;
import be.vdab.entities.MailAccount;
import be.vdab.enums.MailServers;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context=SpringApplication.run(Application.class, args);
		ImapMailReceiver mailReceiver = context.getBean(ImapMailReceiver.class);
		
		ImapIdleChannelAdapter adapter = null;
		try {
			mailReceiver.afterPropertiesSet();
			mailReceiver.setShouldMarkMessagesAsRead(true);
			mailReceiver.setShouldDeleteMessages(false);
			DirectChannel outputChannel = new DirectChannel();
			outputChannel.subscribe(new MessageHandler() {

				@Override
				public void handleMessage(Message<?> message) throws MessagingException {
					Email email = new Email();
					MimeMessage mimeMessage = (MimeMessage) message.getPayload();
					try {
						Multipart multipart = (Multipart) mimeMessage.getContent();
						/*
						 * List<String> headers = new ArrayList<>();
						 * headers.addAll
						 * (Arrays.asList(mimeMessage.getHeader("to")));
						 * headers.
						 * addAll(Arrays.asList(mimeMessage.getHeader("from")));
						 * headers
						 * .addAll(Arrays.asList(mimeMessage.getHeader("subject"
						 * ))); for (String header : headers) {
						 * System.out.println(header); }
						 * System.out.println(bodyPart.getContent());
						 */
						BodyPart bodyPart = multipart.getBodyPart(0);
						email.setOntvanger(mimeMessage.getHeader("to")[0]);
						email.setZender(mimeMessage.getHeader("from")[0]);
						email.setOnderwerp(mimeMessage.getHeader("subject")[0]);
						email.setInhoud((String) bodyPart.getContent());
						System.out.println(email.toString());
					} catch (IOException | javax.mail.MessagingException e) {
						e.printStackTrace();
					}
				}
			});
			ThreadPoolTaskScheduler poolTaskScheduler = new ThreadPoolTaskScheduler();
			poolTaskScheduler.setPoolSize(1);
			poolTaskScheduler.afterPropertiesSet();
			adapter = new ImapIdleChannelAdapter(mailReceiver);
			adapter.setAutoStartup(true);
			adapter.setShouldReconnectAutomatically(true);
			adapter.setTaskScheduler(poolTaskScheduler);
			adapter.setOutputChannel(outputChannel);
			adapter.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				if (adapter != null) {
					adapter.stop();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	@Bean
	ImapMailReceiver receiver() {
		String tweedeacount = "glenn.lefevere.spring";
		MailAccount account = new MailAccount();
		account.setUsername(tweedeacount);
		String temp = "imaps://" + account.getUsername() + ":" + account.getPassword() + "@" + MailServers.GMAIL + ":993/INBOX";
		ImapMailReceiver receiver = new ImapMailReceiver(temp);
		return receiver;
	}

}
