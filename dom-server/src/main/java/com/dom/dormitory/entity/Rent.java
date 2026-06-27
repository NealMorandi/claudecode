package com.dom.dormitory.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 寮費エンティティ（テーブル: rent_records） */
@Data
public class Rent {
    private Long id;
    private Long employeeId;
    private Long residenceId;
    /** 対象年月 YYYY-MM */
    private String yearMonth;
    /** 面積（㎡） */
    private BigDecimal area;
    /** 単価（円/㎡/日） */
    private BigDecimal unitPrice;
    /** 入居日数 */
    private Integer days;
    /** 月の日数 */
    private Integer monthDays;
    /** 寮費（円） */
    private BigDecimal amount;
    /** ステータス: draft / confirmed */
    private String status;
    private LocalDateTime confirmedAt;
    private Integer confirmedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
