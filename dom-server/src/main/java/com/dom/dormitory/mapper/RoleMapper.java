package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper {
    List<Role> selectAll();
    Role selectById(@Param("id") Long id);
    void insert(Role role);
    void update(Role role);
    void deleteById(@Param("id") Long id);
}
