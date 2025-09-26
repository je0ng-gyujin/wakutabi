package com.wakutabi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WakutabiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WakutabiApplication.class, args);
	}

}
