package com.dom.dormitory.service;

import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.EmployeeDTO;
import com.dom.dormitory.entity.vo.EmployeeVO;
import com.dom.dormitory.entity.vo.PageVO;
import java.util.List;

public interface EmployeeService {
    List<EmployeeVO> search(String keyword);
    PageVO<EmployeeVO> list(String keyword, Integer page, Integer pageSize);
    EmployeeVO create(EmployeeDTO dto);
    EmployeeVO update(Long id, EmployeeDTO dto);
    void delete(Long id);
}
