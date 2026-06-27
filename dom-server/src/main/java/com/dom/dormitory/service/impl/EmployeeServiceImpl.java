package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.Employee;
import com.dom.dormitory.entity.Residence;
import com.dom.dormitory.entity.dto.EmployeeDTO;
import com.dom.dormitory.entity.vo.EmployeeVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.EmployeeMapper;
import com.dom.dormitory.mapper.ResidenceMapper;
import com.dom.dormitory.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/** 社員サービス実装 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final ResidenceMapper residenceMapper;

    @Override
    public List<EmployeeVO> search(String keyword) {
        return employeeMapper.selectByKeywordWithStatus(keyword);
    }

    @Override
    public PageVO<EmployeeVO> list(String keyword, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<EmployeeVO> voList = employeeMapper.selectListWithStatus(keyword, offset, pageSize);
        Long total = employeeMapper.countList(keyword);
        PageVO<EmployeeVO> pageVO = new PageVO<>(voList, total);
        // サマリーカード用：全社員に対する集計（フィルターなし）
        pageVO.setPendingCount(employeeMapper.countByType("japan"));
        pageVO.setDoneCount(employeeMapper.countByType("china"));
        pageVO.setConfirmedCount(employeeMapper.countResiding());
        return pageVO;
    }

    @Override
    public EmployeeVO create(EmployeeDTO dto) {
        Employee employee = new Employee();
        copyFromDTO(employee, dto);
        employeeMapper.insert(employee);
        EmployeeVO vo = new EmployeeVO();
        vo.setId(employee.getId());
        vo.setEmployeeNo(employee.getEmployeeNo());
        vo.setName(employee.getName());
        vo.setGender(employee.getGender());
        vo.setType(employee.getType());
        vo.setDepartment(employee.getDepartment());
        vo.setResidenceStatus("none");
        return vo;
    }

    @Override
    public EmployeeVO update(Long id, EmployeeDTO dto) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        copyFromDTO(employee, dto);
        employee.setId(id);
        employeeMapper.update(employee);
        Residence residence = residenceMapper.selectActiveByEmployeeId(id);
        return toVO(employee, residence != null ? "active" : "none");
    }

    @Override
    public void delete(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        Residence residence = residenceMapper.selectActiveByEmployeeId(id);
        if (residence != null) {
            throw new BusinessException(MessageConstant.EMPLOYEE_HAS_RESIDENCE);
        }
        employeeMapper.deleteById(id);
    }

    private void copyFromDTO(Employee employee, EmployeeDTO dto) {
        employee.setEmployeeNo(dto.getEmployeeNo());
        employee.setName(dto.getName());
        employee.setGender(dto.getGender());
        employee.setType(dto.getType());
        employee.setDepartment(dto.getDepartment());
    }

    private EmployeeVO toVO(Employee e, String residenceStatus) {
        EmployeeVO vo = new EmployeeVO();
        vo.setId(e.getId());
        vo.setEmployeeNo(e.getEmployeeNo());
        vo.setName(e.getName());
        vo.setGender(e.getGender());
        vo.setType(e.getType());
        vo.setDepartment(e.getDepartment());
        vo.setFirstUseDate(e.getFirstUseDate());
        vo.setResidenceStatus(residenceStatus);
        vo.setCreatedAt(e.getCreatedAt());
        return vo;
    }
}
