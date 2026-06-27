package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

/** 備品情報VO */
@Data
public class EquipmentVO {
    private Long id;
    private String name;
    private String maker;
    /** カテゴリ: appliance / furniture / bedding / other */
    private String category;
    private String remark;
    private LocalDateTime createdAt;
}
