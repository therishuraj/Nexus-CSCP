package org.razz.notification.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Kafka Consumer Configuration
 * 
 * Configures Kafka consumer for consuming messages from multiple topics:
 * - events: Generic email notifications
 * - orderNotification: Order-related email notifications
 * 
 * @author Backend Engineering Team
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    /**
     * Creates and configures the Kafka ConsumerFactory
     * 
     * @return ConsumerFactory configured for String keys and JSON values
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        log.info("Configuring Kafka ConsumerFactory");
        
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.HashMap");

        log.info("Kafka Consumer Configuration: Bootstrap Servers: kafka:9092, Group ID: consumer-group, Auto Offset Reset: earliest");
        log.debug("Consumer properties: {}", props);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Creates and configures the Kafka Listener Container Factory
     * 
     * @return ConcurrentKafkaListenerContainerFactory for handling concurrent message consumption
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        log.info("Configuring Kafka Listener Container Factory");
        
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        log.info("Kafka Listener Container Factory configured successfully");
        return factory;
    }
}
