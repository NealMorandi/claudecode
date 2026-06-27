package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.CheckoutDTO;
import com.dom.dormitory.entity.dto.ResidenceDTO;
import com.dom.dormitory.entity.dto.ResidenceUpdateDTO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.ResidenceDetailVO;
import com.dom.dormitory.entity.vo.ResidenceVO;
import com.dom.dormitory.service.ResidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 入居コントローラ */
@RestController
@RequestMapping("/residences")
@RequiredArgsConstructor
public class ResidenceController {

    private final ResidenceService residenceService;

    /** 入居一覧 */
    @GetMapping
    public Result<PageVO<ResidenceVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String checkinFrom,
            @RequestParam(required = false) String checkinTo,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(residenceService.list(keyword, status, checkinFrom, checkinTo, page, pageSize), MessageConstant.GET_SUCCESS);
    }

    /** 入居詳細 */
    @GetMapping("/{id}")
    public Result<ResidenceDetailVO> getById(@PathVariable Long id) {
        return Result.success(residenceService.getById(id), MessageConstant.GET_SUCCESS);
    }

    /** 入居登録 */
    @PostMapping
    public Result<ResidenceVO> create(@RequestBody ResidenceDTO dto) {
        return Result.success(residenceService.create(dto), MessageConstant.CREATE_SUCCESS);
    }

    /** 入居情報更新 */
    @PutMapping("/{id}")
    public Result<ResidenceVO> update(@PathVariable Long id, @RequestBody ResidenceUpdateDTO dto) {
        return Result.success(residenceService.update(id, dto), MessageConstant.UPDATE_SUCCESS);
    }

    /** 退寮処理 */
    @PutMapping("/{id}/checkout")
    public Result<Void> checkout(@PathVariable Long id, @RequestBody CheckoutDTO dto) {
        residenceService.checkout(id, dto);
        return Result.success(null, MessageConstant.CHECKOUT_SUCCESS);
    }
}
