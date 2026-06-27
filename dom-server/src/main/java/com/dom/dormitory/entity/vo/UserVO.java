package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

/** ユーザー情報VO */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String name;
    private String role;
    /** 有効フラグ */
    private Boolean active;
    private LocalDateTime createdAt;
}
