package com.dom.dormitory.entity.vo;

import lombok.Data;

/** ログインレスポンスVO */
@Data
public class LoginVO {
    private String token;
    private String username;
    private String name;
    private String role;
}
