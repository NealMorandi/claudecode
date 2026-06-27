package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** コードマスタエンティティ */
@Data
public class CodeMaster {
    private Long id;
    private String category;
    private String code;
    private String name;
    private Integer sortOrder;
    private Integer isDeleted;
    private LocalDateTime createdAt;
}
