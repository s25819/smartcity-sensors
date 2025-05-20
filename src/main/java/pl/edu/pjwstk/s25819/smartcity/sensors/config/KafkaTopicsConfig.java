package pl.edu.pjwstk.s25819.smartcity.sensors.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaTopicsConfig {

    @Value("${spring.kafka.topics.airquality}")
    private String airQualityTopic;
}
