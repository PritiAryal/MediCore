package com.priti.medicalprofileservice.controller;

import com.priti.medicalprofileservice.dto.MedicalProfileRequestDTO;
import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;
import com.priti.medicalprofileservice.dto.validators.CreateMedicalProfileValidationGroup;
import com.priti.medicalprofileservice.service.MedicalProfileService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PostMapping
    public ResponseEntity<MedicalProfileResponseDTO> createMedicalProfile(@Validated({Default.class, CreateMedicalProfileValidationGroup.class}) @RequestBody MedicalProfileRequestDTO medicalProfileRequestDTO) {
        MedicalProfileResponseDTO medicalProfileResponseDTO = medicalProfileService.createMedicalProfile(medicalProfileRequestDTO);
        return ResponseEntity.ok().body(medicalProfileResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalProfileResponseDTO> updateMedicalProfile(@PathVariable UUID id, @Validated({Default.class}) @RequestBody MedicalProfileRequestDTO medicalProfileRequestDTO){
        MedicalProfileResponseDTO medicalProfileResponseDTO = medicalProfileService.updateMedicalProfile(id, medicalProfileRequestDTO);
        return ResponseEntity.ok().body(medicalProfileResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MedicalProfileResponseDTO> deleteMedicalProfile(@PathVariable UUID id){
        MedicalProfileResponseDTO medicalProfileResponseDTO = medicalProfileService.deleteMedicalProfile(id);
        return ResponseEntity.ok().body(medicalProfileResponseDTO);
        //If not return anything just ResponseEntity<void> then you can use ResponseEntity.noContent().build();
    }
}
