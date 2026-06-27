package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.SettingsDTO;
import com.dom.dormitory.entity.vo.SettingsVO;
import com.dom.dormitory.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** システム設定コントローラ */
@RestController
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    /** 設定取得 */
    @GetMapping
    public Result<SettingsVO> get() {
        return Result.success(settingsService.get(), MessageConstant.GET_SUCCESS);
    }

    /** 設定更新 */
    @PutMapping
    public Result<SettingsVO> update(@RequestBody SettingsDTO dto) {
        return Result.success(settingsService.update(dto), MessageConstant.UPDATE_SUCCESS);
    }
}
