package com.civicconnect.entity;

import com.civicconnect.entity.enums.ComplaintCategory;
import com.civicconnect.entity.enums.ImageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A photo attached to a complaint. Only the storage location is kept here, not the file itself.
 * The AI fields stay empty until the detection service analyses the image.
 */
@Entity
@Table(name = "complaint_images")
public class ComplaintImage extends BaseEntity {

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_complaint_images_complaint"))
    private Complaint complaint;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false, length = 20)
    private ImageType imageType = ImageType.REPORT;

    @NotBlank
    @Size(max = 500)
    @Column(name = "storage_path", nullable = false, unique = true, length = 500)
    private String storagePath;

    @Size(max = 255)
    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^image/[A-Za-z0-9.+-]+$", message = "must be an image MIME type")
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Positive
    @Column(name = "file_size_bytes", nullable = false)
    private long fileSizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_detected_category", length = 30)
    private ComplaintCategory aiDetectedCategory;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    @Column(name = "ai_confidence", precision = 5, scale = 4)
    private BigDecimal aiConfidence;

    @Column(name = "ai_processed_at")
    private Instant aiProcessedAt;

    protected ComplaintImage() {
        // Required by JPA
    }

    public ComplaintImage(String storagePath, String contentType, long fileSizeBytes) {
        this.storagePath = storagePath;
        this.contentType = contentType;
        this.fileSizeBytes = fileSizeBytes;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    /** Package-private: use {@link Complaint#addImage} so both sides stay in sync. */
    void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public ImageType getImageType() {
        return imageType;
    }

    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public ComplaintCategory getAiDetectedCategory() {
        return aiDetectedCategory;
    }

    public BigDecimal getAiConfidence() {
        return aiConfidence;
    }

    public Instant getAiProcessedAt() {
        return aiProcessedAt;
    }

    /**
     * Stores a detection result. Category and confidence are always set together,
     * matching the {@code ck_complaint_images_ai_result_complete} constraint.
     */
    public void setAiResult(ComplaintCategory category, BigDecimal confidence, Instant processedAt) {
        this.aiDetectedCategory = category;
        this.aiConfidence = confidence;
        this.aiProcessedAt = processedAt;
    }
}
