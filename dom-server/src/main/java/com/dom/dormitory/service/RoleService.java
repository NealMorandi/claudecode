package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.RoleDTO;
import com.dom.dormitory.entity.vo.RoleVO;
import java.util.List;

public interface RoleService {
    List<RoleVO> list();
    RoleVO create(RoleDTO dto);
    RoleVO update(Long id, RoleDTO dto);
    void delete(Long id);
}
