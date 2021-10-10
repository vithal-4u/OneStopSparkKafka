package com.spark.kafka.poc.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import com.spark.kafka.poc.consumer.config.KafkaConsumerConfig;
import com.spark.kafka.poc.consumer.util.HashTagsUtils;

import scala.Tuple2;

import java.util.*;

@Service
public class SparkConsumerService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SparkConf sparkConf;
    private final KafkaConsumerConfig kafkaConsumerConfig;
    private final Collection<String> topics;

    @Autowired
    public SparkConsumerService(SparkConf sparkConf,
                                KafkaConsumerConfig kafkaConsumerConfig,
                                @Value("${spring.kafka.template.default-topic}") String[] topics) {
        this.sparkConf = sparkConf;
        this.kafkaConsumerConfig = kafkaConsumerConfig;
        this.topics = Arrays.asList(topics);
    }

    public void run(){
        log.debug("Running Spark Consumer Service..");

        // Create context with a 10 seconds batch interval
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(10));

        // Create direct kafka stream with brokers and topics
        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaConsumerConfig.consumerConfigs()));

        // Get the lines, split them into words, count the words and print
        JavaDStream<String> lines = messages.map(stringStringConsumerRecord -> stringStringConsumerRecord.value());

        //Count the tweets and print
        lines
                .count()
                .map(cnt -> "Popular hash tags in last 60 seconds (" + cnt + " total tweets):")
                .print();

		List<String> allRecord = new ArrayList<String>();
        lines.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			
			@Override
			public void call(JavaRDD<String> rdd) throws Exception {
				System.out.println("Inside forEachRDD");
				List<String> listStr = rdd.collect();
				listStr.forEach(new Consumer<String>() {
					@Override
					public void accept(String data) {
						System.out.println("Output Data ----"+data);
						allRecord.add(data);
					}
				});
				FileWriter writer = new FileWriter("D:\\Study_Document\\temp\\output\\Master_dataset.csv");
				for(String s : allRecord) {
					writer.write(s);
					writer.write("\n");
				}
				System.out.println("Master dataset has been created : ");
				writer.close();
			}
		});

        // Start the computation
        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
