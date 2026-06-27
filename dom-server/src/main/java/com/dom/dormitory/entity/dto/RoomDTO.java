package com.dom.dormitory.entity.dto;

import lombok.Data;
import java.math.BigDecimal;

/** 部屋登録・更新DTO */
@Data
public class RoomDTO {
    private String name;
    private BigDecimal area;
    private Integer capacity;
    private Integer rent;
    private String remark;
}
