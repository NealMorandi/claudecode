package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.constant.StatusConstant;
import com.dom.dormitory.entity.User;
import com.dom.dormitory.entity.dto.LoginDTO;
import com.dom.dormitory.entity.vo.LoginVO;
import com.dom.dormitory.entity.vo.UserInfoVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.UserMapper;
import com.dom.dormitory.service.AuthService;
import com.dom.dormitory.util.JwtUtil;
import com.dom.dormitory.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/** 認証サービス実装 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(MessageConstant.LOGIN_FAILED);
        }
        if (user.getActive() == null || user.getActive() == 0) {
            throw new BusinessException(MessageConstant.ACCOUNT_DISABLED);
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        // Redisにトークンを保存（有効期限: 1日）
        redisUtil.set(StatusConstant.REDIS_AUTH_PREFIX + token, user.getUsername(), 86400L, TimeUnit.SECONDS);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        return vo;
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String rawToken = token.substring(7);
            redisUtil.delete(StatusConstant.REDIS_AUTH_PREFIX + rawToken);
        }
    }

    @Override
    public UserInfoVO getProfile(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        return vo;
    }
}
