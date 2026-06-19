package com.property.service;

import com.property.entity.Owner;
import com.property.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    public Owner findById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("业主不存在"));
    }

    public Owner findByUserId(Long userId) {
        return ownerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("未找到关联业主信息"));
    }

    @Transactional
    public Owner create(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Transactional
    public Owner update(Long id, Owner updated) {
        Owner owner = findById(id);
        owner.setName(updated.getName());
        owner.setPhone(updated.getPhone());
        owner.setIdCard(updated.getIdCard());
        owner.setGender(updated.getGender());
        owner.setBirthday(updated.getBirthday());
        owner.setEmail(updated.getEmail());
        owner.setRoomId(updated.getRoomId());
        owner.setOwnerType(updated.getOwnerType());
        owner.setEmergencyContact(updated.getEmergencyContact());
        owner.setEmergencyPhone(updated.getEmergencyPhone());
        return ownerRepository.save(owner);
    }

    @Transactional
    public void delete(Long id) {
        ownerRepository.deleteById(id);
    }

    public List<Owner> searchByName(String name) {
        return ownerRepository.findByNameContaining(name);
    }
}
