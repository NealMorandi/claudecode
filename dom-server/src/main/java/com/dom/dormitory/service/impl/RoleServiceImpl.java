package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.Role;
import com.dom.dormitory.entity.dto.RoleDTO;
import com.dom.dormitory.entity.vo.RoleVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.RoleMapper;
import com.dom.dormitory.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/** ロールサービス実装 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<RoleVO> list() {
        return roleMapper.selectAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public RoleVO create(RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName());
        role.setDisplayName(dto.getDisplayName());
        roleMapper.insert(role);
        return toVO(role);
    }

    @Override
    public RoleVO update(Long id, RoleDTO dto) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        role.setName(dto.getName());
        role.setDisplayName(dto.getDisplayName());
        roleMapper.update(role);
        return toVO(role);
    }

    @Override
    public void delete(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        roleMapper.deleteById(id);
    }

    private RoleVO toVO(Role r) {
        RoleVO vo = new RoleVO();
        vo.setId(r.getId());
        vo.setName(r.getName());
        vo.setDisplayName(r.getDisplayName());
        vo.setCreatedAt(r.getCreatedAt());
        return vo;
    }
}
