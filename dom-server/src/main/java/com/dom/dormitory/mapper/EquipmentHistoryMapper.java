package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.EquipmentHistory;
import com.dom.dormitory.entity.dto.StorageRegisterDTO;
import com.dom.dormitory.entity.vo.EquipmentHistoryVO;
import com.dom.dormitory.entity.vo.StorageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EquipmentHistoryMapper {
    void insert(EquipmentHistory history);
    List<EquipmentHistory> selectByEquipmentId(@Param("equipmentId") Long equipmentId);
    List<EquipmentHistory> selectAll();
    List<EquipmentHistory> selectByCondition(
            @Param("equipmentId") Long equipmentId,
            @Param("action")      String action,
            @Param("offset")      int offset,
            @Param("size")        int size);
    long countByCondition(
            @Param("equipmentId") Long equipmentId,
            @Param("action")      String action);
    List<EquipmentHistory> selectByAction(@Param("action") String action);
    void updateStorageLocation(@Param("id") Long id, @Param("storageLocation") String storageLocation);
    void updateStorageHistory(@Param("id") Long id, @Param("dto") StorageRegisterDTO dto);
    List<EquipmentHistory> selectStorageByDormitoryId(@Param("dormitoryId") Long dormitoryId);
    List<StorageVO> selectStorageListWithJoins(@Param("name") String name, @Param("location") String location);
    List<EquipmentHistoryVO> selectHistoryWithJoins(
            @Param("equipmentId") Long equipmentId,
            @Param("action")      String action,
            @Param("offset")      int offset,
            @Param("size")        int size);
}
