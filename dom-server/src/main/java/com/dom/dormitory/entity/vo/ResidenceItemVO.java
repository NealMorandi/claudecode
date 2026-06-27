package com.dom.dormitory.entity.vo;

import lombok.Data;

/** 入居備品VO */
@Data
public class ResidenceItemVO {
    private Long id;
    private Long residenceId;
    private Long equipmentId;
    private String equipmentName;
    private String category;
    /** 準備済みフラグ: 0=未準備, 1=準備済み */
    private Integer prepared;
    private String storageLocation;
    /** 処分方法: discard / storage / reuse */
    private String disposition;
}
