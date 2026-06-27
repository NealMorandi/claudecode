package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 寮費履歴VO */
@Data
public class RentHistoryVO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeNo;
    private String yearMonth;
    private Integer days;
    private BigDecimal area;
    private BigDecimal unitPrice;
    private Integer monthDays;
    private BigDecimal amount;
    /** ステータス: draft / confirmed */
    private String status;
    private LocalDateTime confirmedAt;
    private String roomName;
    private String dormitoryName;
}
