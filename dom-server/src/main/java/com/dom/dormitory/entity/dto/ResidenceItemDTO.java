package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 入居備品更新DTO */
@Data
public class ResidenceItemDTO {
    private Long id;
    /** 準備済みフラグ: 0=未準備, 1=準備済み */
    private Integer prepared;
    private String storageLocation;
}
