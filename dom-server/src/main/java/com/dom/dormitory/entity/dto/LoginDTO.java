package com.dom.dormitory.entity.dto;

import lombok.Data;

/** ログインリクエストDTO */
@Data
public class LoginDTO {
    private String username;
    private String password;
}
