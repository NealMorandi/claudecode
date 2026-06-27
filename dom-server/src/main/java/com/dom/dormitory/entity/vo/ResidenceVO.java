package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 入居情報VO */
@Data
public class ResidenceVO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeNo;
    private String employeeType;
    private Long roomId;
    private String roomName;
    private String dormitoryName;
    private LocalDate checkinDate;
    private LocalDate expectedCheckoutDate;
    private LocalDate checkoutDate;
    private String checkoutReason;
    /** ステータス: active / checked_out */
    private String status;
    private LocalDateTime createdAt;
}
