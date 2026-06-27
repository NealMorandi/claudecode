package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.DormitoryDTO;
import com.dom.dormitory.entity.vo.DormitoryVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.StorageVO;
import com.dom.dormitory.entity.vo.RoomResidentVO;
import java.util.List;

public interface DormitoryService {
    PageVO<DormitoryVO> list(String name, String type, String prefecture, String status, int page, int pageSize);
    DormitoryVO getById(Long id);
    DormitoryVO create(DormitoryDTO dto);
    DormitoryVO update(Long id, DormitoryDTO dto);
    void delete(Long id);
    List<RoomResidentVO> getRoomResidents(Long id);
    List<StorageVO> getEquipmentByDormitory(Long id);
}
