package com.abcijkxyz.blockchain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.abcijkxyz.blockchain.deamon.GenerateBlocks3;

@SpringBootApplication
@EnableScheduling
public class AccountModelApplication {
//	public class AccountModelApplication implements CommandLineRunner {

	@Autowired
	private GenerateBlocks3 blocks3;

	public static void main(String[] args) {

		SpringApplication.run(AccountModelApplication.class, args);

	}

//	@Override
	public void run(String... args) throws Exception {
		Thread.sleep(4000);
		blocks3.generateBlocks2();
		Thread.sleep(4000);
		blocks3.generateBlocks2();
	}

}
