package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** 入居詳細（備品含む）VO */
@Data
public class ResidenceDetailVO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeNo;
    private String employeeType;
    private LocalDate firstUseDate;
    private Long roomId;
    private String roomName;
    private BigDecimal roomArea;
    private String dormitoryName;
    private LocalDate checkinDate;
    private LocalDate expectedCheckoutDate;
    private LocalDate checkoutDate;
    private String checkoutReason;
    private String status;
    private List<ResidenceItemVO> items;
}
