package com.dom.dormitory.entity.dto;

import lombok.Data;

/** システム設定更新DTO */
@Data
public class SettingsDTO {
    /** 長期利用警告基準年数 */
    private Integer longTermYears;
    /** 日本人社員単価（円/㎡/日） */
    private Integer unitPriceJapan;
    /** 中国人社員単価（円/㎡/日） */
    private Integer unitPriceChina;
}
