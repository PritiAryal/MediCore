package com.priti.medicalprofileservice.service.impl;

import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;
import com.priti.medicalprofileservice.mapper.MedicalProfileMapper;
import com.priti.medicalprofileservice.model.MedicalProfile;
import com.priti.medicalprofileservice.repository.MedicalProfileRepository;
import com.priti.medicalprofileservice.service.MedicalProfileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalProfileServiceImpl implements MedicalProfileService {
    private final MedicalProfileRepository medicalProfileRepository;

    public MedicalProfileServiceImpl(MedicalProfileRepository medicalProfileRepository) {
        this.medicalProfileRepository = medicalProfileRepository;
    }

    public List<MedicalProfileResponseDTO> getMedicalProfiles() {
        List<MedicalProfile> medicalProfiles = medicalProfileRepository.findAll();
        return medicalProfiles.stream()
                .map(MedicalProfileMapper::toDTO).toList();
        //we can also use lamda function instead of method reference i.e. map(mp -> MedicalProfileMapper.toDTO(mp))

    }
}
