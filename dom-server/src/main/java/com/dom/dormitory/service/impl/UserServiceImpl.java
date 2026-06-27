package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.User;
import com.dom.dormitory.entity.dto.PasswordDTO;
import com.dom.dormitory.entity.dto.UserDTO;
import com.dom.dormitory.entity.vo.UserVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.UserMapper;
import com.dom.dormitory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/** ユーザーサービス実装 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserVO> list(String keyword, String role, Integer active) {
        return userMapper.selectList(keyword, role, active)
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public UserVO create(UserDTO dto) {
        User existing = userMapper.selectByUsername(dto.getUsername());
        if (existing != null) {
            throw new BusinessException(MessageConstant.DUPLICATE_ENTRY);
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setActive(1);
        userMapper.insert(user);
        return toVO(user);
    }

    @Override
    public UserVO update(Long id, UserDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        if (dto.getActive() != null) {
            user.setActive(Boolean.TRUE.equals(dto.getActive()) ? 1 : 0);
        }
        userMapper.update(user);
        return toVO(user);
    }

    @Override
    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        userMapper.deleteById(id);
    }

    @Override
    public void updatePassword(Long id, PasswordDTO dto) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        userMapper.updatePassword(id, passwordEncoder.encode(dto.getPassword()));
    }

    private UserVO toVO(User u) {
        UserVO vo = new UserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setName(u.getName());
        vo.setRole(u.getRole());
        vo.setActive(u.getActive() != null && u.getActive() == 1);
        vo.setCreatedAt(u.getCreatedAt());
        return vo;
    }
}
