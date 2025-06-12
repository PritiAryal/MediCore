package com.priti.medicalprofileservice.service;

import com.priti.medicalprofileservice.dto.MedicalProfileRequestDTO;
import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MedicalProfileService {
    List<MedicalProfileResponseDTO> getMedicalProfiles();
    MedicalProfileResponseDTO createMedicalProfile(MedicalProfileRequestDTO medicalProfileRequestDTO);
    MedicalProfileResponseDTO updateMedicalProfile(UUID id, MedicalProfileRequestDTO medicalProfileRequestDTO);
    MedicalProfileResponseDTO deleteMedicalProfile(UUID id);
}
