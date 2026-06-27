package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.Dormitory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DormitoryMapper {
    Dormitory selectById(@Param("id") Long id);
    List<Dormitory> selectAll();
    List<Dormitory> selectByCondition(@Param("name") String name, @Param("type") String type,
                                      @Param("prefecture") String prefecture, @Param("status") String status,
                                      @Param("offset") int offset, @Param("size") int size);
    long countByCondition(@Param("name") String name, @Param("type") String type,
                          @Param("prefecture") String prefecture, @Param("status") String status);
    void insert(Dormitory dormitory);
    void update(Dormitory dormitory);
    void deleteById(@Param("id") Long id);
}
