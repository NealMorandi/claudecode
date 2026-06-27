package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EquipmentMapper {
    Equipment selectById(@Param("id") Long id);
    List<Equipment> selectAll();
    List<Equipment> selectByCondition(@Param("name") String name, @Param("category") String category,
                                      @Param("offset") int offset, @Param("size") int size);
    long countByCondition(@Param("name") String name, @Param("category") String category);
    long countCategoryByCondition(@Param("name") String name, @Param("category") String category);
    long countRemarkByCondition(@Param("name") String name, @Param("category") String category);
    List<java.util.Map<String, Object>> selectByDormitoryId(@Param("dormitoryId") Long dormitoryId);
    void insert(Equipment equipment);
    void update(Equipment equipment);
    void deleteById(@Param("id") Long id);
}
