package com.civicconnect.repository;

import com.civicconnect.entity.ComplaintAssignment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintAssignmentRepository extends JpaRepository<ComplaintAssignment, UUID> {

    Optional<ComplaintAssignment> findByComplaintIdAndActiveTrue(UUID complaintId);
}
