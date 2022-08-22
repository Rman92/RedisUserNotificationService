package com.redis.usernotifyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = { "com.redis.usernotifyservice.pubsub", "com.redis.usernotifyservice*", "com.redis.usernotifyservice.service"})
//@EnableCaching
public class RedisUsernotifyserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisUsernotifyserviceApplication.class, args);
	}

}
