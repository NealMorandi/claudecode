package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/** 寮費計算プレビューVO */
@Data
public class RentCalculateVO {
    private String yearMonth;
    private Integer totalCount;
    private BigDecimal totalAmount;
    private List<RentItemVO> items;

    @Data
    public static class RentItemVO {
        private Long employeeId;
        private String employeeName;
        private String employeeNo;
        private String employeeType;
        private String roomName;
        private String dormitoryName;
        private BigDecimal area;
        private Integer days;
        private BigDecimal unitPrice;
        private Integer monthDays;
        private BigDecimal amount;
    }
}
