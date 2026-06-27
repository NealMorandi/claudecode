package com.dom.dormitory.entity.vo;

import lombok.Data;

/** ログインユーザー情報VO */
@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String name;
    private String role;
}
