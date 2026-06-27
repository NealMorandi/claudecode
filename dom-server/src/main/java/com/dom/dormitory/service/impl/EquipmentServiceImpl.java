package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.*;
import com.dom.dormitory.entity.dto.EquipmentDTO;
import com.dom.dormitory.entity.dto.ResidenceItemDTO;
import com.dom.dormitory.entity.dto.StorageRegisterDTO;
import com.dom.dormitory.entity.vo.EquipmentHistoryVO;
import com.dom.dormitory.entity.vo.EquipmentVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.ResidenceItemVO;
import com.dom.dormitory.entity.vo.StorageVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.*;
import com.dom.dormitory.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** 備品サービス実装 */
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentHistoryMapper equipmentHistoryMapper;
    private final ResidenceItemMapper residenceItemMapper;
    private final ResidenceMapper residenceMapper;
    private final EmployeeMapper employeeMapper;
    private final RoomMapper roomMapper;
    private final DormitoryMapper dormitoryMapper;
    private final UserMapper userMapper;

    private static final java.util.Map<String, String> ACTION_LABELS = java.util.Map.of(
        "created",  "新規登録",
        "assigned", "部屋割り当て",
        "returned", "返却",
        "moved",    "部屋移動",
        "storage",  "倉庫保管",
        "disposed", "廃棄",
        "reused",   "再利用"
    );

    @Override
    public PageVO<EquipmentVO> list(String name, String category, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = equipmentMapper.countByCondition(name, category);
        if (total == 0) return new PageVO<>(List.of(), 0L);
        List<EquipmentVO> voList = equipmentMapper.selectByCondition(name, category, offset, pageSize)
                .stream().map(this::toVO).collect(Collectors.toList());
        PageVO<EquipmentVO> pageVO = new PageVO<>(voList, total);
        pageVO.setDoneCount(equipmentMapper.countCategoryByCondition(name, category));
        pageVO.setPendingCount(equipmentMapper.countRemarkByCondition(name, category));
        return pageVO;
    }

    @Override
    public EquipmentVO create(EquipmentDTO dto) {
        Equipment equipment = new Equipment();
        copyFromDTO(equipment, dto);
        equipmentMapper.insert(equipment);
        return toVO(equipment);
    }

    @Override
    public EquipmentVO update(Long id, EquipmentDTO dto) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        copyFromDTO(equipment, dto);
        equipment.setId(id);
        equipmentMapper.update(equipment);
        return toVO(equipment);
    }

    @Override
    public void delete(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        equipmentMapper.deleteById(id);
    }

    @Override
    public List<StorageVO> listStorage(String name, String location) {
        return equipmentHistoryMapper.selectStorageListWithJoins(name, location);
    }

    @Override
    public void updateHistoryStorageLocation(Long id, String storageLocation) {
        equipmentHistoryMapper.updateStorageLocation(id, storageLocation);
    }

    @Override
    @Transactional
    public void updateHistory(Long id, StorageRegisterDTO dto) {
        equipmentHistoryMapper.updateStorageHistory(id, dto);
    }

    @Override
    public List<ResidenceItemVO> listByResidence(Long residenceId) {
        Residence residence = residenceMapper.selectById(residenceId);
        if (residence == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        return residenceItemMapper.selectWithEquipmentByResidenceId(residenceId);
    }

    @Override
    @Transactional
    public List<ResidenceItemVO> updateItems(Long residenceId, List<ResidenceItemDTO> items) {
        // 全アイテムを1クエリで取得して所有権チェック（N+1回避）
        java.util.Map<Long, ResidenceItem> itemMap = residenceItemMapper.selectByResidenceId(residenceId)
                .stream().collect(Collectors.toMap(ResidenceItem::getId, i -> i));
        List<ResidenceItem> updateList = new ArrayList<>();
        for (ResidenceItemDTO dto : items) {
            ResidenceItem item = itemMap.get(dto.getId());
            if (item != null) {
                item.setPrepared(dto.getPrepared());
                item.setStorageLocation(dto.getStorageLocation());
                updateList.add(item);
            }
        }
        if (!updateList.isEmpty()) {
            residenceItemMapper.batchUpdate(updateList);
        }
        return residenceItemMapper.selectWithEquipmentByResidenceId(residenceId);
    }

    @Override
    @Transactional
    public void registerStorage(Long equipmentId, StorageRegisterDTO dto) {
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        ResidenceItem item = new ResidenceItem();
        item.setEquipmentId(equipmentId);
        item.setPrepared(0);
        item.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        item.setStorageLocation(dto.getStorageLocation());
        item.setDisposition("storage");
        residenceItemMapper.insert(item);

        EquipmentHistory history = new EquipmentHistory();
        history.setEquipmentId(equipmentId);
        history.setAction("storage");
        history.setToRoomId(dto.getRoomId());
        history.setEmployeeId(dto.getEmployeeId());
        history.setEquipmentCondition(dto.getEquipmentCondition());
        history.setStorageLocation(dto.getStorageLocation());
        history.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        history.setNote(dto.getRemark());
        equipmentHistoryMapper.insert(history);
    }

    private void copyFromDTO(Equipment equipment, EquipmentDTO dto) {
        equipment.setName(dto.getName());
        equipment.setMaker(dto.getMaker());
        equipment.setCategory(dto.getCategory());
        equipment.setRemark(dto.getRemark());
    }

    @Override
    public PageVO<EquipmentHistoryVO> listHistory(Long equipmentId, String action, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        long total = equipmentHistoryMapper.countByCondition(equipmentId, action);
        PageVO<EquipmentHistoryVO> pageVO = new PageVO<>(List.of(), total);
        if (total > 0) {
            List<EquipmentHistoryVO> voList = equipmentHistoryMapper
                    .selectHistoryWithJoins(equipmentId, action, offset, pageSize);
            voList.forEach(vo -> vo.setActionLabel(ACTION_LABELS.getOrDefault(vo.getAction(), vo.getAction())));
            pageVO.setList(voList);
        }
        // サマリー統計（アクション区分フィルターに関係なく集計）
        pageVO.setPendingCount(equipmentHistoryMapper.countByCondition(equipmentId, "storage"));
        pageVO.setDoneCount(equipmentHistoryMapper.countByCondition(equipmentId, "assigned"));
        pageVO.setConfirmedCount(equipmentHistoryMapper.countByCondition(equipmentId, "disposed"));
        return pageVO;
    }

    private EquipmentHistoryVO toHistoryVO(EquipmentHistory h) {
        EquipmentHistoryVO vo = new EquipmentHistoryVO();
        vo.setId(h.getId());
        vo.setEquipmentId(h.getEquipmentId());
        vo.setAction(h.getAction());
        vo.setActionLabel(ACTION_LABELS.getOrDefault(h.getAction(), h.getAction()));
        vo.setStorageLocation(h.getStorageLocation());
        vo.setResidenceId(h.getResidenceId());
        vo.setNote(h.getNote());
        if (h.getActionAt() != null) {
            vo.setActionAt(h.getActionAt().toString().replace("T", " "));
        }
        Equipment eq = equipmentMapper.selectById(h.getEquipmentId());
        if (eq != null) vo.setEquipmentName(eq.getName());
        if (h.getFromRoomId() != null) {
            Room r = roomMapper.selectById(h.getFromRoomId());
            if (r != null) vo.setFromRoomName(r.getName());
        }
        if (h.getToRoomId() != null) {
            Room r = roomMapper.selectById(h.getToRoomId());
            if (r != null) vo.setToRoomName(r.getName());
        }
        if (h.getEmployeeId() != null) {
            Employee emp = employeeMapper.selectById(h.getEmployeeId());
            if (emp != null) vo.setEmployeeName(emp.getName());
        }
        if (h.getOperatorId() != null) {
            User u = userMapper.selectById(h.getOperatorId());
            if (u != null) vo.setOperatorName(u.getName());
        }
        return vo;
    }

    private EquipmentVO toVO(Equipment e) {
        EquipmentVO vo = new EquipmentVO();
        vo.setId(e.getId());
        vo.setName(e.getName());
        vo.setMaker(e.getMaker());
        vo.setCategory(e.getCategory());
        vo.setRemark(e.getRemark());
        vo.setCreatedAt(e.getCreatedAt());
        return vo;
    }
}
