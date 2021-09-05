package com.twitter.kafka.poc.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.twitter.api.*;
import org.springframework.stereotype.Service;

import com.twitter.kafka.poc.producer.util.HashTagsUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

@Service
public class StreamTweetEventService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#\\w+");
    public BufferedWriter out;
    private final String kafkaTopic;
    private final Twitter twitter;
    private final KafkaProducer kafkaProducer;

    public StreamTweetEventService (Twitter twitter,
                                    KafkaProducer kafkaProducer,
                                    @Value(value = "${spring.kafka.template.default-topic}") String kafkaTopic){
        this.twitter = twitter;
        this.kafkaProducer = kafkaProducer;
        this.kafkaTopic = kafkaTopic;
    }

    public void run() {
        List<StreamListener> listeners = new ArrayList<StreamListener>();

        StreamListener streamListener = new StreamListener() {

            @Override
            public void onTweet(Tweet tweet) {
                String lang = tweet.getLanguageCode();
                String text = tweet.getText();

                //filter non-English tweets:
                if (!"en".equals(lang)) {
                    return;
                }

                Iterator<String> hashTags = HashTagsUtils.hashTagsFromTweet(text);

                // filter tweets without hashTags:
                if (!hashTags.hasNext()) {
                    return;
                }
                //Send tweet to Kafka topic
                log.info("User '{}', Tweeted : {}, from ; {}", tweet.getUser().getName() , tweet.getText(), tweet.getUser().getLocation());
                kafkaProducer.send(kafkaTopic, tweet.getText());
            }

            @Override
            public void onDelete(StreamDeleteEvent deleteEvent) {
                log.debug("onDelete");
            }

            @Override
            public void onLimit(int numberOfLimitedTweets) {
                log.debug("onLimit");
            }

            @Override
            public void onWarning(StreamWarningEvent warningEvent) {
                log.debug("onLimit");
            }

        };

        //Start Stream when run a service
        listeners.add(streamListener);
        twitter.streamingOperations().sample(listeners);
    }
}
