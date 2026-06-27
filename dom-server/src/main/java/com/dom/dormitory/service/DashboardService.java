package com.dom.dormitory.service;

import com.dom.dormitory.entity.vo.DashboardSummaryVO;
import com.dom.dormitory.entity.vo.DormitoryOccupancyVO;
import com.dom.dormitory.entity.vo.LongTermAlertVO;
import java.util.List;

public interface DashboardService {
    DashboardSummaryVO getSummary();
    List<DormitoryOccupancyVO> getDormitoryOccupancy();
    List<LongTermAlertVO> getTopAlerts(Integer limit);
}
