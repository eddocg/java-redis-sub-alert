package com.eddocg.alerts;

import redis.clients.jedis.Jedis;

public class MessageProducer {
	
	public static final String KEY = "mq-key";
	
	public static void main(String... args) {
		Jedis jedis =  new Jedis("localhost", 6379);
		jedis.connect();
		jedis.rpush(KEY, "{\"workerName\":\"gpu1072\",\"email\":\"eddocg@hotmail.com\"}");
		jedis.disconnect();
		jedis.close();		
	}//End of main

}//End of class