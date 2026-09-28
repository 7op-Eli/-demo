package com.property.repository;

import com.property.entity.GovernmentOfficial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GovernmentOfficialRepository extends JpaRepository<GovernmentOfficial, Long> {
    Optional<GovernmentOfficial> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
