package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 備品登録・更新DTO */
@Data
public class EquipmentDTO {
    private String name;
    private String maker;
    /** カテゴリ: appliance / furniture / bedding / other */
    private String category;
    private String remark;
}
