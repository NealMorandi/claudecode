package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.PasswordDTO;
import com.dom.dormitory.entity.dto.UserDTO;
import com.dom.dormitory.entity.vo.UserVO;
import java.util.List;

public interface UserService {
    List<UserVO> list(String keyword, String role, Integer active);
    UserVO create(UserDTO dto);
    UserVO update(Long id, UserDTO dto);
    void delete(Long id);
    void updatePassword(Long id, PasswordDTO dto);
}
