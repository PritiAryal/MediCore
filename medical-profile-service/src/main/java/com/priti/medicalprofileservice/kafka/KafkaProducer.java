package com.priti.medicalprofileservice.kafka;

import com.priti.medicalprofileservice.model.MedicalProfile;
import medical.profile.events.MedicalProfileEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class KafkaProducer {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    // This is how we define our message types and we use kafkatemplate to send messages to Kafka topics.
    // This is telling kafka that we are going to be sending kafka event that has key of type string and
    // that has a value of type byte array.

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(MedicalProfile medicalProfile) {
        MedicalProfileEvent event = MedicalProfileEvent.newBuilder()
                .setMedicalProfileId(medicalProfile.getId().toString())
                .setName(medicalProfile.getName())
                .setEmail(medicalProfile.getEmail())
                .setEventType("MEDICAL_PROFILE_CREATED")
                .build();
        try {
            kafkaTemplate.send("medical-profile", event.toByteArray()); //to keep size of msg down and to easily convert this msg to object in consumer code.
            log.info("MedicalProfileCreated event sent: {}", event);
        } catch (Exception e) {
            log.error("Error sending MedicalProfileCreated event: {}", event);
        }
    }
}
