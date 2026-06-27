package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.PasswordDTO;
import com.dom.dormitory.entity.dto.UserDTO;
import com.dom.dormitory.entity.vo.UserVO;
import java.util.List;
import com.dom.dormitory.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** ユーザー管理コントローラ */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** ユーザー一覧 */
    @GetMapping
    public Result<List<UserVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String active) {
        Integer activeInt = "true".equals(active) ? Integer.valueOf(1) : "false".equals(active) ? Integer.valueOf(0) : null;
        return Result.success(userService.list(keyword, role, activeInt), MessageConstant.GET_SUCCESS);
    }

    /** ユーザー登録 */
    @PostMapping
    public Result<UserVO> create(@RequestBody UserDTO dto) {
        return Result.success(userService.create(dto), MessageConstant.CREATE_SUCCESS);
    }

    /** ユーザー更新 */
    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return Result.success(userService.update(id, dto), MessageConstant.UPDATE_SUCCESS);
    }

    /** ユーザー削除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success(null, MessageConstant.DELETE_SUCCESS);
    }

    /** パスワード変更 */
    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(@PathVariable Long id, @RequestBody PasswordDTO dto) {
        userService.updatePassword(id, dto);
        return Result.success(null, MessageConstant.PASSWORD_CHANGED);
    }
}
