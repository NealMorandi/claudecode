package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.vo.DashboardSummaryVO;
import com.dom.dormitory.entity.vo.DormitoryOccupancyVO;
import com.dom.dormitory.entity.vo.LongTermAlertVO;
import com.dom.dormitory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** ダッシュボードコントローラ */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /** サマリー取得 */
    @GetMapping("/summary")
    public Result<DashboardSummaryVO> summary() {
        return Result.success(dashboardService.getSummary(), MessageConstant.GET_SUCCESS);
    }

    /** 寮別稼働率取得 */
    @GetMapping("/dormitory-occupancy")
    public Result<List<DormitoryOccupancyVO>> dormitoryOccupancy() {
        return Result.success(dashboardService.getDormitoryOccupancy(), MessageConstant.GET_SUCCESS);
    }
}
