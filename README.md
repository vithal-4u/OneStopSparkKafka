# OneStopSparkKafka
One Stop Project of Spark Kafka integration using Java.

This Project is Combination of below projects:
1. Twitter-producer: This project takes responsibility to get tweets from twitter and filter only language of english and Hashtag tweets. Sends all tweets to kafka topic as a producer.
2. Spark-consumer: This project takes reponsibilty to read tweet from Kafka broker topic and split the tweets and count the words.


Follow Kafka-setup text file setup to start kafka in local before starting the projects.