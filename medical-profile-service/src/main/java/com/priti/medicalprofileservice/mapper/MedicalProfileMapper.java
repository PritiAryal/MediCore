package com.priti.medicalprofileservice.mapper;

import com.priti.medicalprofileservice.dto.MedicalProfileRequestDTO;
import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;
import com.priti.medicalprofileservice.model.MedicalProfile;

import java.time.LocalDate;

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

    public  static MedicalProfile toModel(MedicalProfileRequestDTO medicalProfileRequestDTO){
        MedicalProfile medicalProfile = new MedicalProfile();
        medicalProfile.setName(medicalProfileRequestDTO.getName());
        medicalProfile.setEmail(medicalProfileRequestDTO.getEmail());
        medicalProfile.setAddress(medicalProfileRequestDTO.getAddress());
        medicalProfile.setDateOfBirth(LocalDate.parse(medicalProfileRequestDTO.getDateOfBirth()));
        medicalProfile.setRegisteredDate(LocalDate.parse(medicalProfileRequestDTO.getRegisteredDate()));
        return medicalProfile;
    }
}
