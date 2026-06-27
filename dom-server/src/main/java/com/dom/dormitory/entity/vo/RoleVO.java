package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

/** ロール情報VO */
@Data
public class RoleVO {
    private Long id;
    private String name;
    private String displayName;
    private LocalDateTime createdAt;
}
