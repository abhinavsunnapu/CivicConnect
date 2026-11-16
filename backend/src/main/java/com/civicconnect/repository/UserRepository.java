package com.civicconnect.repository;

import com.civicconnect.entity.User;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    /** Emails are stored lower-case; pass a normalised value or use {@link #findByEmailNormalized}. */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    default Optional<User> findByEmailNormalized(String email) {
        return email == null ? Optional.empty() : findByEmail(email.trim().toLowerCase(Locale.ROOT));
    }
}
