package com.dom.dormitory.entity.dto;

import lombok.Data;

/** ユーザー登録・更新DTO */
@Data
public class UserDTO {
    private String username;
    private String name;
    private String password;
    /** ロール: admin / staff / viewer */
    private String role;
    /** 有効フラグ */
    private Boolean active;
}
