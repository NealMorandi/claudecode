package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 長期利用警告ステータス更新DTO */
@Data
public class LongTermAlertStatusDTO {
    /** ステータス: pending / notified / applied / done */
    private String alertStatus;
}
