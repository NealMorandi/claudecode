package com.dom.dormitory.service.impl;

import com.dom.dormitory.entity.vo.DashboardSummaryVO;
import com.dom.dormitory.entity.vo.DormitoryOccupancyVO;
import com.dom.dormitory.entity.vo.LongTermAlertVO;
import com.dom.dormitory.mapper.EmployeeMapper;
import com.dom.dormitory.mapper.RentMapper;
import com.dom.dormitory.mapper.ResidenceMapper;
import com.dom.dormitory.mapper.RoomMapper;
import com.dom.dormitory.service.DashboardService;
import com.dom.dormitory.service.LongTermAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** ダッシュボードサービス実装 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeMapper employeeMapper;
    private final RoomMapper roomMapper;
    private final RentMapper rentMapper;
    private final ResidenceMapper residenceMapper;
    private final LongTermAlertService longTermAlertService;

    @Override
    public DashboardSummaryVO getSummary() {
        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        DashboardSummaryVO vo = new DashboardSummaryVO();
        vo.setTotalEmployees(employeeMapper.countAll().intValue());
        vo.setOccupiedEmployees(residenceMapper.countActive().intValue());
        vo.setTotalRooms(roomMapper.countTotal().intValue());
        vo.setAvailableRooms(roomMapper.countAvailable().intValue());
        vo.setAlertCount(longTermAlertService.getTopAlerts(Integer.MAX_VALUE).size());
        vo.setMonthlyRent(rentMapper.sumConfirmedByYearMonth(yearMonth));
        Long rentCount = rentMapper.countByYearMonth(yearMonth);
        vo.setRentCount(rentCount.intValue());
        vo.setRentStatus(rentCount > 0 ? "confirmed" : "uncalculated");
        return vo;
    }

    @Override
    public List<DormitoryOccupancyVO> getDormitoryOccupancy() {
        List<Map<String, Object>> raw = roomMapper.selectOccupancyByDormitory();
        return raw.stream().map(m -> {
            DormitoryOccupancyVO vo = new DormitoryOccupancyVO();
            vo.setId(((Number) m.get("id")).longValue());
            vo.setName((String) m.get("name"));
            int total    = toInt(m.get("totalRooms"));
            int occupied = toInt(m.get("occupiedRooms"));
            int available= toInt(m.get("availableRooms"));
            int residents= toInt(m.get("residentCount"));
            int availCap = toInt(m.get("availableCapacity"));
            int totalCap = toInt(m.get("totalCapacity"));
            vo.setTotalRooms(total);
            vo.setOccupiedRooms(occupied);
            vo.setAvailableRooms(available);
            vo.setResidentCount(residents);
            vo.setAvailableCapacity(availCap);
            vo.setTotalCapacity(totalCap);
            vo.setOccupancyRate(totalCap == 0 ? 0.0 : (double) residents / totalCap);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<LongTermAlertVO> getTopAlerts(Integer limit) {
        return longTermAlertService.getTopAlerts(limit);
    }

    private int toInt(Object val) {
        return val == null ? 0 : ((Number) val).intValue();
    }
}
