package com.civicconnect.repository;

import com.civicconnect.entity.ComplaintImage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintImageRepository extends JpaRepository<ComplaintImage, UUID> {

    List<ComplaintImage> findByComplaintIdOrderByCreatedAtAsc(UUID complaintId);
}
