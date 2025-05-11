package pl.edu.pjwstk.s25819.smartcity.sensors.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(producerFactory());
        template.setProducerListener(new ProducerListener<>() {
            @Override
            public void onSuccess(ProducerRecord<Integer, String> record, RecordMetadata metadata) {
                log.info("Wysłano komunikat {} na topic: {}", record.value(), metadata.topic());
            }

            @Override
            public void onError(ProducerRecord<Integer, String> record, RecordMetadata metadata, Exception exception) {
                log.error("Błąd przy wysyłaniu na topic {}: {}", record.topic(), exception.getMessage());
            }
        });
        return template;
    }







}
