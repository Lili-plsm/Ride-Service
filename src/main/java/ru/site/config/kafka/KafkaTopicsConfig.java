package ru.site.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic clientRideRequestsTopic() {
        return TopicBuilder.name("client-ride-requests").partitions(3).build();
    }

    @Bean
    public NewTopic driverRideRequestsTopic() {
        return TopicBuilder.name("driver-ride-requests").partitions(3).build();
    }

    @Bean
    public NewTopic rideAssignmentsTopic() {
        return TopicBuilder.name("ride-assignments").partitions(3).build();
    }

    @Bean
    public NewTopic rideRejectionTopic() {
        return TopicBuilder.name("ride-rejection").partitions(3).build();
    }

    @Bean
    public NewTopic findDriver() {
        return TopicBuilder.name("find-driver").partitions(3).build();
    }

    @Bean
    public NewTopic rideStatusTopic() {
        return TopicBuilder.name("ride-status").partitions(3).build();
    }

    @Bean
    public NewTopic driverLocationTopic() {
        return TopicBuilder.name("driver-status").partitions(5).build();
    }

    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name("payment").partitions(2).build();
    }

    @Bean
    public NewTopic ridecreate() {
        return TopicBuilder.name("ride-create-requests").partitions(2).build();
    }

    @Bean
    public NewTopic driverAssigned() {
        return TopicBuilder.name("driver-assigned").partitions(2).build();
    }
}
