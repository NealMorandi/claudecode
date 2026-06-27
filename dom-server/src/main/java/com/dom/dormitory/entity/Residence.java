package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入居記録エンティティ
 */
@Data
public class Residence {
    private Long id;
    private Long employeeId;
    private Long roomId;
    private LocalDate checkinDate;
    private LocalDate expectedCheckoutDate;
    private LocalDate checkoutDate;
    /** 退寮理由: relocation / retirement / trip_end / other */
    private String checkoutReason;
    /** ステータス: active / checkout */
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
