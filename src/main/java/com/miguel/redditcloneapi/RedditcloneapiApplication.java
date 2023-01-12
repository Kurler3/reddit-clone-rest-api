package com.miguel.redditcloneapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RedditcloneapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditcloneapiApplication.class, args);
	}

}
