package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.Dormitory;
import com.dom.dormitory.entity.Room;
import com.dom.dormitory.entity.dto.RoomDTO;
import com.dom.dormitory.entity.vo.RoomVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.DormitoryMapper;
import com.dom.dormitory.mapper.ResidenceMapper;
import com.dom.dormitory.mapper.RoomMapper;
import com.dom.dormitory.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 部屋サービス実装 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final DormitoryMapper dormitoryMapper;
    private final ResidenceMapper residenceMapper;

    @Override
    public List<RoomVO> listByDormitory(Long dormitoryId) {
        Dormitory dormitory = dormitoryMapper.selectById(dormitoryId);
        if (dormitory == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        List<Map<String, Object>> counts = residenceMapper.selectResidentCountsByDormitoryId(dormitoryId);
        Map<Long, Integer> countMap = counts.stream().collect(Collectors.toMap(
                m -> ((Number) m.get("roomId")).longValue(),
                m -> ((Number) m.get("residentCount")).intValue()
        ));
        return roomMapper.selectByDormitoryId(dormitoryId).stream()
                .map(r -> toVO(r, dormitory.getName(), null, countMap.getOrDefault(r.getId(), 0)))
                .collect(Collectors.toList());
    }

    @Override
    public RoomVO getById(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        Dormitory dormitory = dormitoryMapper.selectById(room.getDormitoryId());
        Integer activeCount = residenceMapper.countActiveByRoomId(id);
        return toVO(room, dormitory != null ? dormitory.getName() : "", null, activeCount != null ? activeCount : 0);
    }

    @Override
    public RoomVO create(Long dormitoryId, RoomDTO dto) {
        Dormitory dormitory = dormitoryMapper.selectById(dormitoryId);
        if (dormitory == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        Room room = new Room();
        room.setDormitoryId(dormitoryId);
        copyFromDTO(room, dto);
        roomMapper.insert(room);
        return toVO(room, dormitory.getName());
    }

    @Override
    public RoomVO update(Long id, RoomDTO dto) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        if (dto.getCapacity() != null) {
            Integer activeCount = residenceMapper.countActiveByRoomId(id);
            if (activeCount != null && dto.getCapacity() < activeCount) {
                throw new BusinessException(400, MessageConstant.ROOM_CAPACITY_BELOW_RESIDENTS);
            }
        }
        copyFromDTO(room, dto);
        roomMapper.update(room);
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        Integer activeCount = residenceMapper.countActiveByRoomId(id);
        if (activeCount != null && activeCount > 0) {
            throw new BusinessException(400, MessageConstant.ROOM_HAS_RESIDENTS);
        }
        roomMapper.deleteById(id);
    }

    @Override
    public List<RoomVO> listAvailable(String date, String gender) {
        return roomMapper.selectAvailable(date != null ? date : "", gender);
    }

    @Override
    public List<RoomVO> listAll() {
        return roomMapper.selectAllNonDeleted().stream().map(r -> {
            Dormitory d = dormitoryMapper.selectById(r.getDormitoryId());
            return toVO(r, d != null ? d.getName() : "");
        }).collect(Collectors.toList());
    }

    private void copyFromDTO(Room room, RoomDTO dto) {
        room.setName(dto.getName());
        room.setArea(dto.getArea());
        room.setCapacity(dto.getCapacity());
        room.setRent(dto.getRent());
        room.setRemark(dto.getRemark());
    }

    private RoomVO toVO(Room r, String dormitoryName) {
        return toVO(r, dormitoryName, null, 0);
    }

    private RoomVO toVO(Room r, String dormitoryName, String dormitoryType) {
        return toVO(r, dormitoryName, dormitoryType, 0);
    }

    private RoomVO toVO(Room r, String dormitoryName, String dormitoryType, Integer residentCount) {
        RoomVO vo = new RoomVO();
        vo.setId(r.getId());
        vo.setDormitoryId(r.getDormitoryId());
        vo.setDormitoryName(dormitoryName);
        vo.setDormitoryType(dormitoryType);
        vo.setName(r.getName());
        vo.setArea(r.getArea());
        vo.setCapacity(r.getCapacity());
        vo.setRent(r.getRent());
        vo.setStatus(r.getStatus());
        vo.setRemark(r.getRemark());
        vo.setCreatedAt(r.getCreatedAt());
        vo.setResidentCount(residentCount != null ? residentCount : 0);
        return vo;
    }
}
