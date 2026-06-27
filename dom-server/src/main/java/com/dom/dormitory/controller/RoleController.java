package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.RoleDTO;
import com.dom.dormitory.entity.vo.RoleVO;
import com.dom.dormitory.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** ロール管理コントローラ */
@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /** ロール一覧 */
    @GetMapping
    public Result<List<RoleVO>> list() {
        return Result.success(roleService.list(), MessageConstant.GET_SUCCESS);
    }

    /** ロール登録 */
    @PostMapping
    public Result<RoleVO> create(@RequestBody RoleDTO dto) {
        return Result.success(roleService.create(dto), MessageConstant.CREATE_SUCCESS);
    }

    /** ロール更新 */
    @PutMapping("/{id}")
    public Result<RoleVO> update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        return Result.success(roleService.update(id, dto), MessageConstant.UPDATE_SUCCESS);
    }

    /** ロール削除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success(null, MessageConstant.DELETE_SUCCESS);
    }
}
