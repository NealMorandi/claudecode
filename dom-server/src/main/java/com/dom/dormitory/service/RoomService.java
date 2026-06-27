package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.RoomDTO;
import com.dom.dormitory.entity.vo.RoomVO;
import java.util.List;

public interface RoomService {
    List<RoomVO> listByDormitory(Long dormitoryId);
    RoomVO getById(Long id);
    RoomVO create(Long dormitoryId, RoomDTO dto);
    RoomVO update(Long id, RoomDTO dto);
    void delete(Long id);
    List<RoomVO> listAvailable(String date, String gender);
    List<RoomVO> listAll();
}
