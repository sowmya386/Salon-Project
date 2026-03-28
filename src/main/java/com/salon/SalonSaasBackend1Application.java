package com.salon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SalonSaasBackend1Application {

	public static void main(String[] args) {
		SpringApplication.run(SalonSaasBackend1Application.class, args);
	}

}
