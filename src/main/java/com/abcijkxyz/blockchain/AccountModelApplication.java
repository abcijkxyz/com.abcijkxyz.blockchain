package com.abcijkxyz.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccountModelApplication {

	public static void main(String[] args) {

		SpringApplication.run(AccountModelApplication.class, args);

	}

}
