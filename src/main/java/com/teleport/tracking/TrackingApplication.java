package com.teleport.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class TrackingApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TrackingApplication.class, args);
		System.out.println("Active profile(s): " + Arrays.toString(context.getEnvironment().getActiveProfiles()));
		System.out.println("Redis URL: " + context.getEnvironment().getProperty("spring.redis.url"));
	}

}
