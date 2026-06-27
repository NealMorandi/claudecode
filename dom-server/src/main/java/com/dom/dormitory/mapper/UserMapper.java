package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    User selectByUsername(@Param("username") String username);
    User selectById(@Param("id") Long id);
    List<User> selectList(@Param("keyword") String keyword, @Param("role") String role, @Param("active") Integer active);
    void insert(User user);
    void update(User user);
    void deleteById(@Param("id") Long id);
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}
