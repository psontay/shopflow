package com.shopflow.shared.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(DefaultErrorHandler.class)
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                                                                                    (r, e) -> {
                                                                                        log.error(
                                                                                                "Message in topic {} failed after retries. Moving to DLT...",
                                                                                                r.topic(),
                                                                                                e);
                                                                                        return new org.apache.kafka.common.TopicPartition(
                                                                                                r.topic() + ".DLT",
                                                                                                r.partition());
                                                                                    });

        FixedBackOff backOff = new FixedBackOff(1000L, 3); // retry
        return new DefaultErrorHandler(recoverer, backOff);
    }

}
