package com.nexuscart.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base Event class for Kafka messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent implements Serializable {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String source;

    public static BaseEvent create(String eventType, String source) {
        return BaseEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .source(source)
                .build();
    }
}
