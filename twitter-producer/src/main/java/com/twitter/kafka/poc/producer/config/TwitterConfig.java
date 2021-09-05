package com.twitter.kafka.poc.producer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
public class TwitterConfig {
    @Bean
    public TwitterTemplate twitterTemplate(final @Value("${spring.social.twitter.appId}") String appId,
                                           final @Value("${spring.social.twitter.appSecret}") String appSecret,
                                           final @Value("${spring.social.twitter.accessToken}") String accessToken,
                                           final @Value("${spring.social.twitter.accessTokenSecret}") String accessTokenSecret) {
        return new TwitterTemplate(appId, appSecret,accessToken,accessTokenSecret);
    }
}
