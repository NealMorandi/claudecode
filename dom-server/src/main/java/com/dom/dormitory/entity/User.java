package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * システムユーザーエンティティ
 */
@Data
public class User {
    private Long id;
    private String username;
    private String name;
    private String password;
    /** ロール: admin / staff / viewer */
    private String role;
    /** 有効フラグ: 1=有効, 0=無効 */
    private Integer active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
