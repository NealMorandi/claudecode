package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.vo.CodeMasterVO;
import com.dom.dormitory.service.CodeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** コードマスタコントローラ */
@RestController
@RequestMapping("/code-master")
@RequiredArgsConstructor
public class CodeMasterController {

    private final CodeMasterService codeMasterService;

    /** カテゴリ別コード一覧取得 */
    @GetMapping
    public Result<List<CodeMasterVO>> list(@RequestParam String category) {
        return Result.success(codeMasterService.listByCategory(category), MessageConstant.GET_SUCCESS);
    }
}
