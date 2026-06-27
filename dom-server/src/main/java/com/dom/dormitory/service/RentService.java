package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.RentCalculateDTO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.RentCalculateVO;
import com.dom.dormitory.entity.vo.RentHistoryVO;
import jakarta.servlet.http.HttpServletResponse;

public interface RentService {
    RentCalculateVO calculate(RentCalculateDTO dto);
    void confirm(RentCalculateDTO dto);
    PageVO<RentHistoryVO> history(Long employeeId, String yearMonth, String status, Integer page, Integer pageSize);
    RentHistoryVO getById(Long id);
    void export(String keyword, String yearMonth, HttpServletResponse response);
    void cancelByYearMonth(String yearMonth);
}
