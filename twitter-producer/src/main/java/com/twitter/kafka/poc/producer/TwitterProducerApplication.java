package com.twitter.kafka.poc.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.twitter.kafka.poc.producer.service.StreamTweetEventService;

@SpringBootApplication
public class TwitterProducerApplication implements CommandLineRunner {

	@Autowired
	StreamTweetEventService tweetEventService;

	public static void main(String[] args) {
		SpringApplication.run(TwitterProducerApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		tweetEventService.run();
	}
}
