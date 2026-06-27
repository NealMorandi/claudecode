package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** ロールエンティティ */
@Data
public class Role {
    private Long id;
    private String name;
    private String displayName;
    private LocalDateTime createdAt;
}
