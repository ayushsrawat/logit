package com.ayushsrawat.logit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogitApplication {

	static void main(String[] args) {
		SpringApplication.run(LogitApplication.class, args);
	}

}
