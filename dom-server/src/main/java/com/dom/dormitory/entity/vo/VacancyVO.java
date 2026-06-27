package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.math.BigDecimal;

/** 空室情報VO */
@Data
public class VacancyVO {
    private Long id;
    private String name;
    private BigDecimal area;
    private Integer capacity;
    private Integer currentOccupancy;
    private String dormitoryName;
    private String dormitoryType;
    private String lastCheckout;
}
