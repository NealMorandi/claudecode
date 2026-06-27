package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 入居備品エンティティ
 */
@Data
public class ResidenceItem {
    private Long id;
    private Long residenceId;
    private Long equipmentId;
    /** 準備済みフラグ: 0=未準備, 1=準備済み */
    private Integer prepared;
    private Integer quantity;
    private String storageLocation;
    /** 処分方法: discard / storage / reuse */
    private String disposition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
