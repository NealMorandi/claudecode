package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.*;
import com.dom.dormitory.entity.dto.DormitoryDTO;
import com.dom.dormitory.entity.vo.DormitoryVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.RoomResidentVO;
import com.dom.dormitory.entity.vo.StorageVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.*;
import com.dom.dormitory.service.DormitoryService;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 寮サービス実装 */
@Service
@RequiredArgsConstructor
public class DormitoryServiceImpl implements DormitoryService {

    private final DormitoryMapper dormitoryMapper;
    private final RoomMapper roomMapper;
    private final ResidenceMapper residenceMapper;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentHistoryMapper equipmentHistoryMapper;
    private final EmployeeMapper employeeMapper;
    private final SystemSettingMapper systemSettingMapper;

    @Override
    public PageVO<DormitoryVO> list(String name, String type, String prefecture, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = dormitoryMapper.countByCondition(name, type, prefecture, status);
        if (total == 0) return new PageVO<>(List.of(), 0L);
        List<Dormitory> rows = dormitoryMapper.selectByCondition(name, type, prefecture, status, offset, pageSize);
        List<Map<String, Object>> occupancy = roomMapper.selectOccupancyByDormitory();
        Map<Long, Map<String, Object>> occupancyMap = occupancy.stream()
                .collect(Collectors.toMap(m -> ((Number) m.get("id")).longValue(), m -> m));
        List<Room> allRooms = roomMapper.selectAllNonDeleted();
        Map<Long, List<Room>> roomsByDormitory = allRooms.stream()
                .collect(Collectors.groupingBy(Room::getDormitoryId));
        List<DormitoryVO> voList = rows.stream()
                .map(d -> toVO(d, occupancyMap.get(d.getId()), roomsByDormitory.getOrDefault(d.getId(), List.of())))
                .collect(Collectors.toList());
        return new PageVO<>(voList, total);
    }

    @Override
    public DormitoryVO getById(Long id) {
        Dormitory dormitory = dormitoryMapper.selectById(id);
        if (dormitory == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        List<Map<String, Object>> occupancy = roomMapper.selectOccupancyByDormitory();
        Map<Long, Map<String, Object>> occupancyMap = occupancy.stream()
                .collect(Collectors.toMap(m -> ((Number) m.get("id")).longValue(), m -> m));
        List<Room> rooms = roomMapper.selectByDormitoryId(id);
        return toVO(dormitory, occupancyMap.get(id), rooms);
    }

    @Override
    @Transactional
    public DormitoryVO create(DormitoryDTO dto) {
        Dormitory dormitory = new Dormitory();
        copyFromDTO(dormitory, dto);
        dormitoryMapper.insert(dormitory);
        autoCreateRooms(dormitory.getId(), dto.getLayout());
        return toVO(dormitory, null, List.of());
    }

    private void autoCreateRooms(Long dormitoryId, String layout) {
        if (layout == null || layout.isEmpty()) return;
        int roomCount = Character.getNumericValue(layout.charAt(0));
        if (roomCount <= 0 || roomCount > 9) return;
        for (int i = 1; i <= roomCount; i++) {
            Room room = new Room();
            room.setDormitoryId(dormitoryId);
            room.setName("10" + i + "号室");
            room.setArea(new BigDecimal("20"));
            room.setCapacity(1);
            room.setRent(0);
            roomMapper.insert(room);
        }
    }

    @Override
    public DormitoryVO update(Long id, DormitoryDTO dto) {
        Dormitory dormitory = dormitoryMapper.selectById(id);
        if (dormitory == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        copyFromDTO(dormitory, dto);
        dormitory.setId(id);
        dormitoryMapper.update(dormitory);
        return getById(id);
    }

    @Override
    public void delete(Long id) {
        Dormitory dormitory = dormitoryMapper.selectById(id);
        if (dormitory == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        Long activeCount = residenceMapper.countActiveByDormitoryId(id);
        if (activeCount != null && activeCount > 0) {
            throw new BusinessException(400, MessageConstant.DORMITORY_HAS_RESIDENTS);
        }
        dormitoryMapper.deleteById(id);
    }

    @Override
    public List<RoomResidentVO> getRoomResidents(Long id) {
        SystemSetting settings = systemSettingMapper.selectAll();
        BigDecimal unitPriceJapan = (settings != null && settings.getUnitPriceJapan() != null)
                ? settings.getUnitPriceJapan() : new BigDecimal("2000");
        BigDecimal unitPriceChina = (settings != null && settings.getUnitPriceChina() != null)
                ? settings.getUnitPriceChina() : BigDecimal.ZERO;

        LocalDate today      = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        List<Map<String, Object>> rows = roomMapper.selectRoomResidentsByDormitoryId(id);
        return rows.stream().map(m -> {
            RoomResidentVO vo = new RoomResidentVO();
            vo.setRoomId(m.get("roomId") == null ? null : ((Number) m.get("roomId")).longValue());
            vo.setRoomName((String) m.get("roomName"));
            vo.setCapacity(toInt(m.get("capacity")));
            vo.setResidentCount(toInt(m.get("residentCount")));
            vo.setResidenceId(m.get("residenceId") == null ? null : ((Number) m.get("residenceId")).longValue());
            vo.setEmployeeId(m.get("employeeId") == null ? null : ((Number) m.get("employeeId")).longValue());
            vo.setEmployeeName((String) m.get("employeeName"));
            vo.setEmployeeType((String) m.get("employeeType"));
            vo.setDepartment((String) m.get("department"));
            vo.setCheckinDate((String) m.get("checkinDate"));
            vo.setCheckoutDate((String) m.get("checkoutDate"));

            // 当月寮費計算（入居者がいる場合のみ）
            if (vo.getEmployeeId() != null && vo.getCheckinDate() != null) {
                LocalDate checkin  = LocalDate.parse(vo.getCheckinDate());
                LocalDate checkout = vo.getCheckoutDate() != null
                        ? LocalDate.parse(vo.getCheckoutDate()) : today;
                LocalDate from = checkin.isAfter(monthStart) ? checkin : monthStart;
                LocalDate to   = checkout.isBefore(today)    ? checkout : today;
                int days = (int)(to.toEpochDay() - from.toEpochDay() + 1);
                if (days > 0) {
                    BigDecimal unitPrice = "japan".equals(vo.getEmployeeType())
                            ? unitPriceJapan : unitPriceChina;
                    vo.setCurrentMonthDays(days);
                    vo.setCurrentMonthRent(unitPrice.multiply(BigDecimal.valueOf(days)));
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StorageVO> getEquipmentByDormitory(Long id) {
        List<EquipmentHistory> historyItems = equipmentHistoryMapper.selectStorageByDormitoryId(id);
        return historyItems.stream().map(item -> {
            StorageVO vo = new StorageVO();
            vo.setHistoryId(item.getId());
            vo.setEquipmentId(item.getEquipmentId());
            vo.setStorageLocation(item.getStorageLocation());
            vo.setQuantity(item.getQuantity());
            vo.setEquipmentCondition(item.getEquipmentCondition());
            vo.setEmployeeId(item.getEmployeeId());
            vo.setRemark(item.getNote());
            Equipment eq = equipmentMapper.selectById(item.getEquipmentId());
            if (eq != null) {
                vo.setEquipmentName(eq.getName());
                vo.setMaker(eq.getMaker());
                vo.setCategory(eq.getCategory());
            }
            if (item.getEmployeeId() != null) {
                Employee emp = employeeMapper.selectById(item.getEmployeeId());
                if (emp != null) vo.setEmployeeName(emp.getName());
            }
            if (item.getToRoomId() != null) {
                Room room = roomMapper.selectById(item.getToRoomId());
                if (room != null) vo.setRoomName(room.getName());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private int toInt(Object val) {
        return val == null ? 0 : ((Number) val).intValue();
    }

    private void copyFromDTO(Dormitory dormitory, DormitoryDTO dto) {
        dormitory.setName(dto.getName());
        dormitory.setType(dto.getType());
        dormitory.setPrefecture(dto.getPrefecture());
        dormitory.setAddress(dto.getAddress());
        dormitory.setLayout(dto.getLayout());
        dormitory.setRemark(dto.getRemark());
    }

    private DormitoryVO toVO(Dormitory d, Map<String, Object> occupancy, List<Room> rooms) {
        DormitoryVO vo = new DormitoryVO();
        vo.setId(d.getId());
        vo.setName(d.getName());
        vo.setType(d.getType());
        vo.setStatus(d.getStatus());
        vo.setPrefecture(d.getPrefecture());
        vo.setAddress(d.getAddress());
        vo.setLayout(d.getLayout());
        vo.setRemark(d.getRemark());
        vo.setCreatedAt(d.getCreatedAt());
        if (occupancy != null) {
            vo.setTotalRooms(toInt(occupancy.get("totalRooms")));
            vo.setAvailableRooms(toInt(occupancy.get("availableRooms")));
            vo.setResidentCount(toInt(occupancy.get("residentCount")));
            vo.setAvailableCapacity(toInt(occupancy.get("availableCapacity")));
            vo.setTotalCapacity(toInt(occupancy.get("totalCapacity")));
        } else {
            vo.setTotalRooms(0);
            vo.setAvailableRooms(0);
            vo.setResidentCount(0);
            vo.setAvailableCapacity(0);
            vo.setTotalCapacity(0);
        }
        List<DormitoryVO.RoomRentVO> roomRents = rooms.stream().map(r -> {
            DormitoryVO.RoomRentVO rr = new DormitoryVO.RoomRentVO();
            rr.setName(r.getName());
            rr.setRent(r.getRent());
            return rr;
        }).collect(Collectors.toList());
        vo.setRoomRents(roomRents);
        return vo;
    }
}
