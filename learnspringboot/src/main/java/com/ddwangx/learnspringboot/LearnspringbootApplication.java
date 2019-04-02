package com.ddwangx.learnspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//启动spring的定时机制
@EnableScheduling
public class LearnspringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnspringbootApplication.class, args);
	}

}
