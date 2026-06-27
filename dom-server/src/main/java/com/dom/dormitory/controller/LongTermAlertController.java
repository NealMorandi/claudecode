package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.LongTermAlertStatusDTO;
import com.dom.dormitory.entity.vo.LongTermAlertVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.service.LongTermAlertService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 長期利用警告コントローラ */
@RestController
@RequestMapping("/long-term-alerts")
@RequiredArgsConstructor
public class LongTermAlertController {

    private final LongTermAlertService longTermAlertService;

    /** 警告一覧 */
    @GetMapping
    public Result<PageVO<LongTermAlertVO>> list(
            @RequestParam(required = false) Integer minYears,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(longTermAlertService.list(minYears, status, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** ステータス更新 */
    @PatchMapping("/{employeeId}/status")
    public Result<Void> updateStatus(@PathVariable Long employeeId,
                                      @RequestBody LongTermAlertStatusDTO dto) {
        longTermAlertService.updateStatus(employeeId, dto);
        return Result.success(null, MessageConstant.UPDATE_SUCCESS);
    }

    /** エクスポート */
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) Integer minYears,
                       @RequestParam(required = false, defaultValue = "") String status) {
        longTermAlertService.export(response, minYears, status);
    }
}
