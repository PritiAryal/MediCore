package com.priti.medicalprofileservice.exception;

import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;

public class MedicalProfileNotFoundException extends RuntimeException{

    public MedicalProfileNotFoundException(String message){
        super(message);
    }
}
