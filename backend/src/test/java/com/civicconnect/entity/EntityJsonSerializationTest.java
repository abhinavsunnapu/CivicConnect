package com.civicconnect.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.civicconnect.entity.enums.ComplaintCategory;
import com.civicconnect.entity.enums.ComplaintStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

/**
 * Entities should never be returned by the API (DTOs are), but if one is serialised by mistake it must not
 * recurse forever through bidirectional relationships or leak the password hash.
 */
class EntityJsonSerializationTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void complaintWithBidirectionalChildrenSerialisesWithoutRecursion() {
        User reporter = new User("Asha", "asha@example.com", "secret-hash");
        Complaint complaint = new Complaint(reporter, "Broken streetlight", "Dark for a week",
                ComplaintCategory.STREETLIGHT, new BigDecimal("12.9716"), new BigDecimal("77.5946"));
        complaint.addImage(new ComplaintImage("complaints/light.jpg", "image/jpeg", 100));
        complaint.addStatusHistory(new ComplaintStatusHistory(null, ComplaintStatus.SUBMITTED, reporter, null));

        String json = mapper.writeValueAsString(complaint);

        assertThat(json)
                .contains("\"title\":\"Broken streetlight\"")
                .doesNotContain("images", "statusHistory", "reporter", "secret-hash");
    }

    @Test
    void userJsonExcludesPasswordHash() {
        String json = mapper.writeValueAsString(new User("Asha", "asha@example.com", "secret-hash"));

        assertThat(json).contains("asha@example.com").doesNotContain("passwordHash", "secret-hash");
    }
}
