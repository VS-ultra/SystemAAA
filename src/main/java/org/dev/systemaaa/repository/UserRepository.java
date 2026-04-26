package org.dev.systemaaa.repository;

import org.dev.systemaaa.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByusername(void attr0);

    boolean existsByEmail(void attr0);
}
