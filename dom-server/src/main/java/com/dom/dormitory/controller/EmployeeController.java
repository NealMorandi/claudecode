package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.EmployeeDTO;
import com.dom.dormitory.entity.vo.EmployeeVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** 社員コントローラ */
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /** 社員検索（ドロップダウン用） */
    @GetMapping
    public Result<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        if (page == null) {
            // ページなし→検索用
            List<EmployeeVO> list = employeeService.search(keyword);
            return Result.success(list, MessageConstant.GET_SUCCESS);
        }
        PageVO<EmployeeVO> pageVO = employeeService.list(keyword, page, pageSize);
        return Result.success(pageVO, MessageConstant.GET_SUCCESS);
    }

    /** 社員登録 */
    @PostMapping
    public Result<EmployeeVO> create(@RequestBody EmployeeDTO dto) {
        return Result.success(employeeService.create(dto), MessageConstant.CREATE_SUCCESS);
    }

    /** 社員更新 */
    @PutMapping("/{id}")
    public Result<EmployeeVO> update(@PathVariable Long id, @RequestBody EmployeeDTO dto) {
        return Result.success(employeeService.update(id, dto), MessageConstant.UPDATE_SUCCESS);
    }

    /** 社員削除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success(null, MessageConstant.DELETE_SUCCESS);
    }
}
