package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 長期利用警告VO */
@Data
public class LongTermAlertVO {
    private Long employeeId;
    private String employeeNo;
    private String employeeName;
    private String department;
    private String roomName;
    private String dormitoryName;
    private LocalDate checkinDate;
    /** 初回入居日（employee.firstUseDate） */
    private LocalDate firstUseDate;
    /** 通算利用年数（firstUseDateから算出） */
    private Integer yearsOfStay;
    /** 警告ステータス: pending / notified / applied / done */
    private String alertStatus;
    private LocalDateTime updatedAt;
    /** 閾値超過からの日数 */
    private Integer overdueDays;
    /** 超過開始日（firstUseDate + thresholdYears） */
    private LocalDate overdueSince;
}
