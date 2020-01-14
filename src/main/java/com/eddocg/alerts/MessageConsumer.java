package com.eddocg.alerts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class MessageConsumer {
	private static final int TIMEOUT = 0;
	private static final String KEY = "alerts";
	static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

	public static void main(String...args) {
		logger.info("Starting Message Consumer");

		//Create a new connection to Redis with Host and Port
		Jedis jedis = new Jedis("192.168.1.9",9595);
		logger.debug("Jedis Object created");

		jedis.connect();
		logger.info("Connection to Redis DB successfully");

		if(jedis.isConnected()) {
			//Create the SendHTMLEmail object
			SendHTMLEmail sender = new SendHTMLEmail();

			//If there are messages in the Key enter while loop
			while(jedis.llen(KEY)>0) {
				logger.info("Message found on the queue");
				List<String> messages = jedis.blpop(TIMEOUT, KEY);
				logger.info("Received message with key:{} with value:{}", messages.get(0),messages.get(1));
				sender.sendMail(messages.get(1));
			}//End of while
			logger.info("Finished sending all emails");
		}//End of if

		jedis.disconnect();
		logger.info("Closing RedisDB connection");
		jedis.close();
		logger.info("End of Message Consumer");

	}//End of main
}//End of Class