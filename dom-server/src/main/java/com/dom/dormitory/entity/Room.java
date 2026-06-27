package com.dom.dormitory.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 部屋マスタエンティティ
 */
@Data
public class Room {
    private Long id;
    private Long dormitoryId;
    private String name;
    private BigDecimal area;
    private Integer capacity;
    /** 部屋個別寮費（円、nullは共通設定を使用） */
    private Integer rent;
    /** ステータス: vacant / occupied */
    private String status;
    private String remark;
    /** 論理削除: 0=有効, 1=削除 */
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
