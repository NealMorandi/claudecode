package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 寮費計算・確定DTO */
@Data
public class RentCalculateDTO {
    /** 対象年月 YYYY-MM */
    private String yearMonth;
}
