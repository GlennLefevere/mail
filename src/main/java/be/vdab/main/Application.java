package be.vdab.main;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mail.ImapIdleChannelAdapter;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import be.vdab.entities.MailAccount;
import be.vdab.enums.mailServers;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
	
	public static void main(String[] args) {
		final Logger logger = Logger.getLogger(Application.class.getName());
		SpringApplication.run(Application.class, args);
		String tweedeacount = "glenn.lefevere.spring";
		MailAccount account = new MailAccount();
		account.setUsername(tweedeacount);
		ImapIdleChannelAdapter adapter = null;
		try {
			String temp = "imaps://" + account.getUsername() + ":" + account.getPassword()+"@" + mailServers.GMAIL + ":993/INBOX";
			ImapMailReceiver mailReceiver = new ImapMailReceiver(temp);
			mailReceiver.afterPropertiesSet();
			mailReceiver.setShouldMarkMessagesAsRead(true);
			mailReceiver.setShouldDeleteMessages(false);
			DirectChannel outputChannel = new DirectChannel();
			outputChannel.subscribe(new MessageHandler() {
				
				@Override
				public void handleMessage(Message<?> message) throws MessagingException {
						logger.info("" + message);
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
			try{
				adapter.stop();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}

}
