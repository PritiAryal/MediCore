package com.priti.medicalprofileservice.service;

import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;

import java.util.List;

public interface MedicalProfileService {
    List<MedicalProfileResponseDTO> getMedicalProfiles();

}
