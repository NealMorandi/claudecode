package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.vo.VacancyVO;
import com.dom.dormitory.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 空室コントローラ */
@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    /** 空室一覧 */
    @GetMapping
    public Result<List<VacancyVO>> list(
            @RequestParam(required = false, defaultValue = "") String gender,
            @RequestParam(required = false, defaultValue = "") String date) {
        return Result.success(vacancyService.list(gender, date), MessageConstant.GET_SUCCESS);
    }
}
