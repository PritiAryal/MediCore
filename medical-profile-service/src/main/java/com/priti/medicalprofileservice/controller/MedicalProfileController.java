package com.priti.medicalprofileservice.controller;

import com.priti.medicalprofileservice.dto.MedicalProfileRequestDTO;
import com.priti.medicalprofileservice.dto.MedicalProfileResponseDTO;
import com.priti.medicalprofileservice.dto.validators.CreateMedicalProfileValidationGroup;
import com.priti.medicalprofileservice.service.MedicalProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/medical-profiles")
@Tag(name = "Medical Profile", description = "API related to managing medical profiles")
public class MedicalProfileController {
    private final MedicalProfileService medicalProfileService;

    public MedicalProfileController(MedicalProfileService medicalProfileService) {
        this.medicalProfileService = medicalProfileService;
    }

    @GetMapping
    @Operation(summary = "Get all medical profiles", description = "Retrieve a list of all medical profiles")
    public ResponseEntity<List<MedicalProfileResponseDTO>> getMedicalProfiles() {
        List<MedicalProfileResponseDTO> medicalProfiles = medicalProfileService.getMedicalProfiles();
        return ResponseEntity.ok().body(medicalProfiles);
    }

    @PostMapping
    @Operation(summary = "Create a new medical profile", description = "Create a new medical profile with the provided details")
    public ResponseEntity<MedicalProfileResponseDTO> createMedicalProfile(@Validated({Default.class, CreateMedicalProfileValidationGroup.class}) @RequestBody MedicalProfileRequestDTO medicalProfileRequestDTO) {
        MedicalProfileResponseDTO medicalProfileResponseDTO = medicalProfileService.createMedicalProfile(medicalProfileRequestDTO);
        return ResponseEntity.ok().body(medicalProfileResponseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing medical profile", description = "Update the details of an existing medical profile by ID")
    public ResponseEntity<MedicalProfileResponseDTO> updateMedicalProfile(@PathVariable UUID id, @Validated({Default.class}) @RequestBody MedicalProfileRequestDTO medicalProfileRequestDTO){
        MedicalProfileResponseDTO medicalProfileResponseDTO = medicalProfileService.updateMedicalProfile(id, medicalProfileRequestDTO);
        return ResponseEntity.ok().body(medicalProfileResponseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a medical profile", description = "Delete a medical profile by ID")
    public ResponseEntity<MedicalProfileResponseDTO> deleteMedicalProfile(@PathVariable UUID id){
        MedicalProfileResponseDTO medicalProfileResponseDTO = medicalProfileService.deleteMedicalProfile(id);
        return ResponseEntity.ok().body(medicalProfileResponseDTO);
        //If not return anything just ResponseEntity<void> then you can use ResponseEntity.noContent().build();
    }
}
