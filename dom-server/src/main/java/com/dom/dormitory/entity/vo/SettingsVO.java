package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** システム設定VO */
@Data
public class SettingsVO {
    private Integer    longTermYears;
    private BigDecimal unitPriceJapan;
    private BigDecimal unitPriceChina;
    private LocalDateTime updatedAt;
}
