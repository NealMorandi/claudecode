package com.dom.dormitory.controller;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.common.Result;
import com.dom.dormitory.entity.dto.LoginDTO;
import com.dom.dormitory.entity.vo.LoginVO;
import com.dom.dormitory.entity.vo.UserInfoVO;
import com.dom.dormitory.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 認証コントローラ */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** ログイン */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto), MessageConstant.LOGIN_SUCCESS);
    }

    /** ログアウト */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return Result.success(null, MessageConstant.LOGOUT_SUCCESS);
    }

    /** ログインユーザー情報取得 */
    @GetMapping("/profile")
    public Result<UserInfoVO> profile(Authentication authentication) {
        String username = authentication.getName();
        return Result.success(authService.getProfile(username), MessageConstant.GET_SUCCESS);
    }
}
