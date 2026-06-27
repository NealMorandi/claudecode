package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.EquipmentDTO;
import com.dom.dormitory.entity.dto.ResidenceItemDTO;
import com.dom.dormitory.entity.dto.StorageRegisterDTO;
import com.dom.dormitory.entity.vo.EquipmentHistoryVO;
import com.dom.dormitory.entity.vo.EquipmentVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.ResidenceItemVO;
import com.dom.dormitory.entity.vo.StorageVO;
import java.util.List;

public interface EquipmentService {
    PageVO<EquipmentVO> list(String name, String category, int page, int pageSize);
    EquipmentVO create(EquipmentDTO dto);
    EquipmentVO update(Long id, EquipmentDTO dto);
    void delete(Long id);
    List<StorageVO> listStorage(String name, String location);
    List<ResidenceItemVO> listByResidence(Long residenceId);
    List<ResidenceItemVO> updateItems(Long residenceId, List<ResidenceItemDTO> items);
    PageVO<EquipmentHistoryVO> listHistory(Long equipmentId, String action, int page, int pageSize);
    void registerStorage(Long equipmentId, StorageRegisterDTO dto);
    void updateHistoryStorageLocation(Long id, String storageLocation);
    void updateHistory(Long id, StorageRegisterDTO dto);
}
