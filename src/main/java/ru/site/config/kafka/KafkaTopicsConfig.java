package ru.site.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

  @Bean
  NewTopic clientRideRequestsTopic() {
    return TopicBuilder.name("client-ride-requests").partitions(3).build();
  }

  @Bean
  NewTopic driverRideRequestsTopic() {
    return TopicBuilder.name("driver-ride-requests").partitions(3).build();
  }

  @Bean
  NewTopic rideAssignmentsTopic() {
    return TopicBuilder.name("ride-assignments").partitions(3).build();
  }

  @Bean
  NewTopic rideRejectionTopic() {
    return TopicBuilder.name("ride-rejection").partitions(3).build();
  }

  @Bean
  NewTopic findDriver() {
    return TopicBuilder.name("find-driver").partitions(3).build();
  }

  @Bean
  NewTopic rideStatusTopic() {
    return TopicBuilder.name("ride-status").partitions(3).build();
  }

  @Bean
  NewTopic driverLocationTopic() {
    return TopicBuilder.name("driver-status").partitions(3).build();
  }

  @Bean
  NewTopic paymentsTopic() {
    return TopicBuilder.name("payment").partitions(3).build();
  }

  @Bean
  NewTopic ridecreate() {
    return TopicBuilder.name("ride-create-requests").partitions(3).build();
  }

  @Bean
  NewTopic driverAssigned() {
    return TopicBuilder.name("driver-assigned").partitions(3).build();
  }
}
