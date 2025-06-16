package com.priti.medicalanalyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import medical.profile.events.MedicalProfileEvent;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    @KafkaListener(topics="medical-profile", groupId="medical-analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            MedicalProfileEvent medicalProfileEvent = MedicalProfileEvent.parseFrom(event);
            // we can perform any business logic related to analytics here by calling service layer or database

            log.info("Received Medical Profile Event: [MedicalProfileId={},Name={},Email={}]",
                    medicalProfileEvent.getMedicalProfileId(),
                    medicalProfileEvent.getName(),
                    medicalProfileEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event {}", e.getMessage());
        }
    }
}
