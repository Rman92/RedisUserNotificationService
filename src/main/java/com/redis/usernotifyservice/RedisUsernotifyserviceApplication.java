package com.redis.usernotifyservice;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Initial main class to start the app
 *
 * @author Rushabh Khandare
 */
@SpringBootApplication(scanBasePackages = { "com.redis.usernotifyservice.pubsub", "com.redis.usernotifyservice*", "com.redis.usernotifyservice.service"})
//@EnableCaching
@Log4j2
public class RedisUsernotifyserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(RedisUsernotifyserviceApplication.class, args);
		log.info("RedisUsernotifyserviceApplication started!!");
	}
}
