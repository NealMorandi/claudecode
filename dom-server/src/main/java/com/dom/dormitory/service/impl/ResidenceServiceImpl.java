package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.*;
import com.dom.dormitory.entity.dto.CheckoutDTO;
import com.dom.dormitory.entity.dto.ResidenceDTO;
import com.dom.dormitory.entity.dto.ResidenceUpdateDTO;
import com.dom.dormitory.entity.vo.*;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.*;
import com.dom.dormitory.service.ResidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** 入居サービス実装 */
@Service
@RequiredArgsConstructor
public class ResidenceServiceImpl implements ResidenceService {

    private final ResidenceMapper residenceMapper;
    private final EmployeeMapper employeeMapper;
    private final RoomMapper roomMapper;
    private final DormitoryMapper dormitoryMapper;
    private final ResidenceItemMapper residenceItemMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    public PageVO<ResidenceVO> list(String keyword, String status, String checkinFrom, String checkinTo, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<ResidenceVO> voList = residenceMapper.selectList(keyword, status, checkinFrom, checkinTo, offset, pageSize);
        Long total = residenceMapper.countList(keyword, status, checkinFrom, checkinTo);
        return new PageVO<>(voList, total);
    }

    @Override
    public ResidenceDetailVO getById(Long id) {
        Residence residence = residenceMapper.selectById(id);
        if (residence == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        return toDetailVO(residence);
    }

    @Override
    @Transactional
    public ResidenceVO create(ResidenceDTO dto) {
        Employee employee = employeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        Residence existing = residenceMapper.selectActiveByEmployeeId(dto.getEmployeeId());
        if (existing != null) {
            throw new BusinessException(MessageConstant.EMPLOYEE_HAS_RESIDENCE);
        }
        Room room = roomMapper.selectById(dto.getRoomId());
        if (room == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        int currentOcc = residenceMapper.countActiveByRoomId(dto.getRoomId());
        if (currentOcc >= room.getCapacity()) {
            throw new BusinessException(MessageConstant.ROOM_OCCUPIED);
        }

        Residence residence = new Residence();
        residence.setEmployeeId(dto.getEmployeeId());
        residence.setRoomId(dto.getRoomId());
        residence.setCheckinDate(LocalDate.parse(dto.getCheckinDate()));
        if (dto.getExpectedCheckoutDate() != null && !dto.getExpectedCheckoutDate().isEmpty()) {
            residence.setExpectedCheckoutDate(LocalDate.parse(dto.getExpectedCheckoutDate()));
        }
        residenceMapper.insert(residence);

        // 定員に達した場合のみoccupiedに変更
        String newStatus = (currentOcc + 1 >= room.getCapacity()) ? "occupied" : "available";
        roomMapper.updateStatus(dto.getRoomId(), newStatus);

        // 初回入居日の設定
        if (employee.getFirstUseDate() == null) {
            employeeMapper.updateFirstUseDate(employee.getId(), residence.getCheckinDate());
        }

        // 全備品の入居備品レコードを作成
        List<Equipment> allEquipment = equipmentMapper.selectAll();
        if (!allEquipment.isEmpty()) {
            List<ResidenceItem> items = allEquipment.stream().map(eq -> {
                ResidenceItem item = new ResidenceItem();
                item.setResidenceId(residence.getId());
                item.setEquipmentId(eq.getId());
                item.setPrepared(0);
                item.setQuantity(1);
                return item;
            }).collect(Collectors.toList());
            residenceItemMapper.batchInsert(items);
        }

        return toVO(residence);
    }

    @Override
    @Transactional
    public ResidenceVO update(Long id, ResidenceUpdateDTO dto) {
        Residence residence = residenceMapper.selectById(id);
        if (residence == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        if (dto.getExpectedCheckoutDate() != null && !dto.getExpectedCheckoutDate().isEmpty()) {
            residence.setExpectedCheckoutDate(LocalDate.parse(dto.getExpectedCheckoutDate()));
        }
        residenceMapper.update(residence);
        return toVO(residence);
    }

    @Override
    @Transactional
    public void checkout(Long id, CheckoutDTO dto) {
        if (dto.getCheckoutDate() == null || dto.getCheckoutDate().isBlank()) {
            throw new BusinessException(MessageConstant.PARAM_INVALID);
        }
        if (dto.getCheckoutReason() == null || dto.getCheckoutReason().isBlank()) {
            throw new BusinessException(MessageConstant.PARAM_INVALID);
        }

        LocalDate checkoutDate;
        try {
            checkoutDate = LocalDate.parse(dto.getCheckoutDate());
        } catch (DateTimeParseException e) {
            throw new BusinessException(MessageConstant.PARAM_INVALID);
        }

        Residence residence = residenceMapper.selectById(id);
        if (residence == null || !"active".equals(residence.getStatus())) {
            throw new BusinessException(MessageConstant.RESIDENCE_NOT_ACTIVE);
        }
        if (checkoutDate.isBefore(residence.getCheckinDate())) {
            throw new BusinessException(MessageConstant.CHECKOUT_DATE_INVALID);
        }

        residence.setCheckoutDate(checkoutDate);
        residence.setCheckoutReason(dto.getCheckoutReason());
        residence.setStatus("checked_out");
        residenceMapper.update(residence);

        // 退寮後の残在居数で部屋ステータスを正確に更新
        int remaining = residenceMapper.countActiveByRoomId(residence.getRoomId());
        roomMapper.updateStatus(residence.getRoomId(), remaining > 0 ? "occupied" : "available");

        // 備品の処分情報を更新（全件1クエリ取得→Mapで所有権チェック、N+1回避）
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            java.util.Map<Long, ResidenceItem> itemMap = residenceItemMapper.selectByResidenceId(id)
                    .stream().collect(Collectors.toMap(ResidenceItem::getId, i -> i));
            List<ResidenceItem> updateList = new ArrayList<>();
            for (CheckoutDTO.CheckoutItemDTO itemDto : dto.getItems()) {
                ResidenceItem item = itemMap.get(itemDto.getId());
                if (item != null) {
                    item.setDisposition(itemDto.getDisposition());
                    item.setStorageLocation("storage".equals(itemDto.getDisposition())
                            ? itemDto.getStorageLocation() : null);
                    updateList.add(item);
                }
            }
            if (!updateList.isEmpty()) {
                residenceItemMapper.batchUpdate(updateList);
            }
        }
    }

    private ResidenceVO toVO(Residence r) {
        ResidenceVO vo = new ResidenceVO();
        vo.setId(r.getId());
        vo.setEmployeeId(r.getEmployeeId());
        vo.setRoomId(r.getRoomId());
        vo.setCheckinDate(r.getCheckinDate());
        vo.setExpectedCheckoutDate(r.getExpectedCheckoutDate());
        vo.setCheckoutDate(r.getCheckoutDate());
        vo.setCheckoutReason(r.getCheckoutReason());
        vo.setStatus(r.getStatus());
        vo.setCreatedAt(r.getCreatedAt());
        // 社員・部屋・寮名の取得
        Employee employee = employeeMapper.selectById(r.getEmployeeId());
        if (employee != null) {
            vo.setEmployeeName(employee.getName());
            vo.setEmployeeNo(employee.getEmployeeNo());
            vo.setEmployeeType(employee.getType());
        }
        Room room = roomMapper.selectById(r.getRoomId());
        if (room != null) {
            vo.setRoomName(room.getName());
            Dormitory dormitory = dormitoryMapper.selectById(room.getDormitoryId());
            if (dormitory != null) {
                vo.setDormitoryName(dormitory.getName());
            }
        }
        return vo;
    }

    private ResidenceDetailVO toDetailVO(Residence r) {
        ResidenceDetailVO vo = new ResidenceDetailVO();
        vo.setId(r.getId());
        vo.setEmployeeId(r.getEmployeeId());
        vo.setCheckinDate(r.getCheckinDate());
        vo.setExpectedCheckoutDate(r.getExpectedCheckoutDate());
        vo.setCheckoutDate(r.getCheckoutDate());
        vo.setCheckoutReason(r.getCheckoutReason());
        vo.setStatus(r.getStatus());
        Employee employee = employeeMapper.selectById(r.getEmployeeId());
        if (employee != null) {
            vo.setEmployeeName(employee.getName());
            vo.setEmployeeNo(employee.getEmployeeNo());
            vo.setEmployeeType(employee.getType());
            vo.setFirstUseDate(employee.getFirstUseDate());
        }
        Room room = roomMapper.selectById(r.getRoomId());
        if (room != null) {
            vo.setRoomId(room.getId());
            vo.setRoomName(room.getName());
            vo.setRoomArea(room.getArea());
            Dormitory dormitory = dormitoryMapper.selectById(room.getDormitoryId());
            if (dormitory != null) {
                vo.setDormitoryName(dormitory.getName());
            }
        }
        vo.setItems(residenceItemMapper.selectWithEquipmentByResidenceId(r.getId()));
        return vo;
    }
}
