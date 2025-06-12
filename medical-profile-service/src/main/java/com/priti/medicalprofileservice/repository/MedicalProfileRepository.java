package com.priti.medicalprofileservice.repository;

import com.priti.medicalprofileservice.model.MedicalProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicalProfileRepository extends JpaRepository<MedicalProfile, UUID> {
    boolean existsByEmail(String email);
}
