package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.LoginDTO;
import com.dom.dormitory.entity.vo.LoginVO;
import com.dom.dormitory.entity.vo.UserInfoVO;

public interface AuthService {
    LoginVO login(LoginDTO dto);
    void logout(String token);
    UserInfoVO getProfile(String username);
}
