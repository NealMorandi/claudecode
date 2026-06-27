package com.dom.dormitory.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** システム設定エンティティ（テーブル: settings、単一行設計） */
@Data
public class SystemSetting {
    private Integer id;
    private Integer longTermYears;
    private BigDecimal unitPriceJapan;
    private BigDecimal unitPriceChina;
    private LocalDateTime updatedAt;
}
