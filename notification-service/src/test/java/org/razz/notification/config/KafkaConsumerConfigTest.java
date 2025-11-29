package org.razz.notification.config;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Unit tests for KafkaConsumerConfig
 * 
 * Tests cover:
 * - Consumer factory configuration
 * - Listener container factory configuration
 * - Kafka consumer properties validation
 */
class KafkaConsumerConfigTest {

    private final KafkaConsumerConfig kafkaConsumerConfig = new KafkaConsumerConfig();

    @Test
    void testConsumerFactoryCreation() {
        // Act
        ConsumerFactory<String, Object> consumerFactory = kafkaConsumerConfig.consumerFactory();

        // Assert
        assertThat(consumerFactory).isNotNull();
        assertThat(consumerFactory).isInstanceOf(ConsumerFactory.class);
    }

    @Test
    void testConsumerFactoryConfiguration() {
        // Act
        ConsumerFactory<String, Object> consumerFactory = kafkaConsumerConfig.consumerFactory();
        Map<String, Object> configProps = consumerFactory.getConfigurationProperties();

        // Assert - Verify bootstrap servers
        assertThat(configProps).containsEntry(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "kafka:9092"
        );

        // Assert - Verify group ID
        assertThat(configProps).containsEntry(
            ConsumerConfig.GROUP_ID_CONFIG,
            "consumer-group"
        );

        // Assert - Verify auto offset reset
        assertThat(configProps).containsEntry(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"
        );

        // Assert - Verify key deserializer
        assertThat(configProps).containsEntry(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
        );

        // Assert - Verify value deserializer
        assertThat(configProps).containsEntry(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JsonDeserializer.class
        );

        // Assert - Verify JSON deserializer trusted packages
        assertThat(configProps).containsEntry(
            JsonDeserializer.TRUSTED_PACKAGES,
            "*"
        );

        // Assert - Verify JSON deserializer type info headers
        assertThat(configProps).containsEntry(
            JsonDeserializer.USE_TYPE_INFO_HEADERS,
            false
        );

        // Assert - Verify JSON deserializer default type
        assertThat(configProps).containsEntry(
            JsonDeserializer.VALUE_DEFAULT_TYPE,
            "java.util.HashMap"
        );
    }

    @Test
    void testKafkaListenerContainerFactoryCreation() {
        // Act
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
            kafkaConsumerConfig.kafkaListenerContainerFactory();

        // Assert
        assertThat(factory).isNotNull();
        assertThat(factory).isInstanceOf(ConcurrentKafkaListenerContainerFactory.class);
    }

    @Test
    void testKafkaListenerContainerFactoryUsesConsumerFactory() {
        // Arrange
        ConsumerFactory<String, Object> consumerFactory = kafkaConsumerConfig.consumerFactory();

        // Act
        ConcurrentKafkaListenerContainerFactory<String, Object> listenerFactory = 
            kafkaConsumerConfig.kafkaListenerContainerFactory();

        // Assert - Verify factory uses the consumer factory
        assertThat(listenerFactory).isNotNull();
        assertThat(listenerFactory.getConsumerFactory()).isNotNull();
        
        // Verify configuration is consistent
        Map<String, Object> configProps = listenerFactory.getConsumerFactory().getConfigurationProperties();
        assertThat(configProps).containsEntry(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            "kafka:9092"
        );
    }
}
