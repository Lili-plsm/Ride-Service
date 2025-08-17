package ru.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableJpaAuditing
@EnableConfigurationProperties(JwtSettings.class)
@SpringBootApplication
@EnableCaching
public class RideApplication {
  public static void main(String[] args) {
    SpringApplication.run(RideApplication.class, args);
  }
}
