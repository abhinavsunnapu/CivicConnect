package com.civicconnect.repository;

import com.civicconnect.entity.Complaint;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {

    Optional<Complaint> findByReferenceNumber(String referenceNumber);
}
