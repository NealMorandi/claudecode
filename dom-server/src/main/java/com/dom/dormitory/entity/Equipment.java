package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 備品マスタエンティティ
 */
@Data
public class Equipment {
    private Long id;
    private String name;
    private String maker;
    /** カテゴリ: appliance / furniture / bedding / other */
    private String category;
    private String remark;
    /** 論理削除: 0=有効, 1=削除 */
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
