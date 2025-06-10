package com.priti.medicalprofileservice.controller;

import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;
import com.priti.medicalprofileservice.service.MedicalProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/medical-profiles")
public class MedicalProfileController {
    private final MedicalProfileService medicalProfileService;

    public MedicalProfileController(MedicalProfileService medicalProfileService) {
        this.medicalProfileService = medicalProfileService;
    }

    @GetMapping
    public ResponseEntity<List<MedicalProfileResponseDTO>> getMedicalProfiles() {
        List<MedicalProfileResponseDTO> medicalProfiles = medicalProfileService.getMedicalProfiles();
        return ResponseEntity.ok().body(medicalProfiles);
    }
}
