package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.vo.OperationLogVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.service.OperationLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 操作ログコントローラ */
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class LogController {

    private final OperationLogService operationLogService;

    /** 操作ログ一覧 */
    @GetMapping
    public Result<PageVO<OperationLogVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(operationLogService.list(keyword, action, from, to, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** 操作ログエクスポート */
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            HttpServletResponse response) {
        operationLogService.export(keyword, action, from, to, response);
    }
}
