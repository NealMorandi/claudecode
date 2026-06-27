package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 長期利用警告ステータスエンティティ */
@Data
public class LongTermAlertStatus {
    private Long id;
    private Long employeeId;
    /** ステータス: pending / notified / applied / done */
    private String alertStatus;
    private LocalDateTime updatedAt;
}
