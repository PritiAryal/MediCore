package com.priti.medicalprofileservice.service.impl;

import com.priti.medicalprofileservice.dto.MedicalProfileRequestDTO;
import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;
import com.priti.medicalprofileservice.exception.EmailAlreadyExistsException;
import com.priti.medicalprofileservice.exception.MedicalProfileNotFoundException;
import com.priti.medicalprofileservice.grpc.MedicalBillingServiceGrpcClient;
import com.priti.medicalprofileservice.kafka.KafkaProducer;
import com.priti.medicalprofileservice.mapper.MedicalProfileMapper;
import com.priti.medicalprofileservice.model.MedicalProfile;
import com.priti.medicalprofileservice.repository.MedicalProfileRepository;
import com.priti.medicalprofileservice.service.MedicalProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MedicalProfileServiceImpl implements MedicalProfileService {
    private final MedicalProfileRepository medicalProfileRepository;

    private final MedicalBillingServiceGrpcClient medicalBillingServiceGrpcClient;

    private final KafkaProducer kafkaProducer;

    public MedicalProfileServiceImpl(MedicalProfileRepository medicalProfileRepository, MedicalBillingServiceGrpcClient medicalBillingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.medicalProfileRepository = medicalProfileRepository;
        this.medicalBillingServiceGrpcClient = medicalBillingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<MedicalProfileResponseDTO> getMedicalProfiles() {
        List<MedicalProfile> medicalProfiles = medicalProfileRepository.findAll();
        return medicalProfiles.stream()
                .map(MedicalProfileMapper::toDTO).toList();
        //we can also use lamda function instead of method reference i.e. map(mp -> MedicalProfileMapper.toDTO(mp))
    }

    public MedicalProfileResponseDTO createMedicalProfile(MedicalProfileRequestDTO medicalProfileRequestDTO){
        if(medicalProfileRepository.existsByEmail(medicalProfileRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A medical profile with this email " + medicalProfileRequestDTO.getEmail()+ " already exists");
        }
        MedicalProfile medicalProfile = medicalProfileRepository.save(MedicalProfileMapper.toModel(medicalProfileRequestDTO));

        medicalBillingServiceGrpcClient.createMedicalBillingAccount(
                medicalProfile.getId().toString(),
                medicalProfile.getName(),
                medicalProfile.getEmail()
        );

        kafkaProducer.sendEvent(medicalProfile);

        return MedicalProfileMapper.toDTO(medicalProfile);
        //It converts new profile details from client i.e reqestdto to medical profile entity then save it in db and convert entity to responsedto and return it.


    }

    public MedicalProfileResponseDTO updateMedicalProfile(UUID id, MedicalProfileRequestDTO medicalProfileRequestDTO){
        MedicalProfile medicalProfile = medicalProfileRepository.findById(id).orElseThrow(() -> new MedicalProfileNotFoundException("Medical Profile not found with ID: " + id));
        if(medicalProfileRepository.existsByEmailAndIdNot(medicalProfileRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A medical profile with this email " + medicalProfileRequestDTO.getEmail()+ " already exists");
        }
        medicalProfile.setName(medicalProfileRequestDTO.getName());
        medicalProfile.setEmail(medicalProfileRequestDTO.getEmail());
        medicalProfile.setAddress(medicalProfileRequestDTO.getAddress());
        medicalProfile.setDateOfBirth(LocalDate.parse(medicalProfileRequestDTO.getDateOfBirth()));
        MedicalProfile updatedMedicalProfile = medicalProfileRepository.save(medicalProfile);
        return MedicalProfileMapper.toDTO(updatedMedicalProfile);
    }

    public MedicalProfileResponseDTO deleteMedicalProfile(UUID id){
        MedicalProfile medicalProfile = medicalProfileRepository.findById(id).orElseThrow(() -> new MedicalProfileNotFoundException("Medical Profile not found with ID: " + id));
        medicalProfileRepository.deleteById(id);
        return MedicalProfileMapper.toDTO(medicalProfile);
    }
}
