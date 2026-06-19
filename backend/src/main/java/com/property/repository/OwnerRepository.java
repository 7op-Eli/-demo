package com.property.repository;

import com.property.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByUserId(Long userId);
    Optional<Owner> findByPhone(String phone);
    List<Owner> findByNameContaining(String name);
    List<Owner> findByRoomId(Long roomId);
}
