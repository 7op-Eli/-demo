package com.property.service;

import com.property.entity.Building;
import com.property.entity.Room;
import com.property.repository.BuildingRepository;
import com.property.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingRoomService {

    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;

    // ========== 楼栋 ==========

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    public Building getBuilding(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("楼栋不存在"));
    }

    @Transactional
    public Building createBuilding(Building building) {
        return buildingRepository.save(building);
    }

    @Transactional
    public Building updateBuilding(Long id, Building updated) {
        Building b = getBuilding(id);
        b.setName(updated.getName());
        b.setAddress(updated.getAddress());
        b.setTotalFloors(updated.getTotalFloors());
        b.setTotalRooms(updated.getTotalRooms());
        return buildingRepository.save(b);
    }

    // ========== 房间 ==========

    public List<Room> getRoomsByBuilding(Long buildingId) {
        return roomRepository.findByBuildingId(buildingId);
    }

    public Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
    }

    @Transactional
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long id, Room updated) {
        Room r = getRoom(id);
        r.setUnitNo(updated.getUnitNo());
        r.setRoomNo(updated.getRoomNo());
        r.setFloor(updated.getFloor());
        r.setArea(updated.getArea());
        r.setOwnerName(updated.getOwnerName());
        r.setOwnerPhone(updated.getOwnerPhone());
        r.setStatus(updated.getStatus());
        return roomRepository.save(r);
    }
}
