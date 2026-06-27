package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.ImportExecuteDTO;
import com.dom.dormitory.entity.dto.ImportValidateDTO;
import com.dom.dormitory.entity.vo.ImportExecuteVO;
import com.dom.dormitory.entity.vo.ImportUploadVO;
import com.dom.dormitory.entity.vo.ImportValidateVO;
import com.dom.dormitory.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** インポートコントローラ */
@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    /** ファイルアップロード */
    @PostMapping("/upload")
    public Result<ImportUploadVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "residence") String type) {
        return Result.success(importService.upload(file, type), MessageConstant.UPLOAD_SUCCESS);
    }

    /** データ検証 */
    @PostMapping("/validate")
    public Result<ImportValidateVO> validate(@RequestBody ImportValidateDTO dto) {
        return Result.success(importService.validate(dto), MessageConstant.VALIDATE_SUCCESS);
    }

    /** インポート実行 */
    @PostMapping("/execute")
    public Result<ImportExecuteVO> execute(@RequestBody ImportExecuteDTO dto) {
        return Result.success(importService.execute(dto), MessageConstant.IMPORT_SUCCESS);
    }

    /** インポートレポート取得 */
    @GetMapping("/report/{jobId}")
    public Result<ImportExecuteVO> report(@PathVariable String jobId) {
        return Result.success(importService.getReport(jobId), MessageConstant.GET_SUCCESS);
    }
}
