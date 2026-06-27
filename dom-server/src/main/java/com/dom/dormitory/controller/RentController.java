package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.RentCalculateDTO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.RentCalculateVO;
import com.dom.dormitory.entity.vo.RentHistoryVO;
import com.dom.dormitory.service.RentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 寮費コントローラ */
@RestController
@RequestMapping("/rent")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    /** 寮費計算プレビュー */
    @PostMapping("/calculate")
    public Result<RentCalculateVO> calculate(@RequestBody RentCalculateDTO dto) {
        return Result.success(rentService.calculate(dto), MessageConstant.CALCULATE_SUCCESS);
    }

    /** 寮費確定 */
    @PostMapping("/confirm")
    public Result<Void> confirm(@RequestBody RentCalculateDTO dto) {
        rentService.confirm(dto);
        return Result.success(null, MessageConstant.CONFIRM_SUCCESS);
    }

    /** 寮費履歴一覧 */
    @GetMapping("/history")
    public Result<PageVO<RentHistoryVO>> history(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(rentService.history(employeeId, yearMonth, status, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** 確定取消（対象月全員分を論理削除） */
    @DeleteMapping("/cancel/{yearMonth}")
    public Result<Void> cancel(@PathVariable String yearMonth) {
        rentService.cancelByYearMonth(yearMonth);
        return Result.success(null, "確定を取消しました");
    }

    /** 寮費詳細取得 */
    @GetMapping("/{id}")
    public Result<RentHistoryVO> getById(@PathVariable Long id) {
        return Result.success(rentService.getById(id), MessageConstant.GET_SUCCESS);
    }

    /** 寮費エクスポート */
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String yearMonth,
            HttpServletResponse response) {
        rentService.export(keyword, yearMonth, response);
    }
}
