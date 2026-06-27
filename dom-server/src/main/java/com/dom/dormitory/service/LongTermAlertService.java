package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.LongTermAlertStatusDTO;
import com.dom.dormitory.entity.vo.LongTermAlertVO;
import com.dom.dormitory.entity.vo.PageVO;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface LongTermAlertService {
    PageVO<LongTermAlertVO> list(Integer minYears, String status, Integer page, Integer pageSize);
    void updateStatus(Long employeeId, LongTermAlertStatusDTO dto);
    void export(HttpServletResponse response, Integer minYears, String status);
    List<LongTermAlertVO> getTopAlerts(Integer limit);
}
