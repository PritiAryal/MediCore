package com.priti.medicalprofileservice.mapper;

import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;
import com.priti.medicalprofileservice.model.MedicalProfile;

public class MedicalProfileMapper {
    //This mapper class is used to convert from entity to DTO
    public static MedicalProfileResponseDTO toDTO(MedicalProfile medicalProfile) {
        MedicalProfileResponseDTO medicalProfileDTO = new MedicalProfileResponseDTO();
        medicalProfileDTO.setId(medicalProfile.getId().toString());
        medicalProfileDTO.setName(medicalProfile.getName());
        medicalProfileDTO.setEmail(medicalProfile.getEmail());
        medicalProfileDTO.setAddress(medicalProfile.getAddress());
        medicalProfileDTO.setDateOfBirth(medicalProfile.getDateOfBirth().toString());
        return medicalProfileDTO;
    }
}
