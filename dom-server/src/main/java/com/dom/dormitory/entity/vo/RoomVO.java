package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 部屋情報VO */
@Data
public class RoomVO {
    private Long id;
    private Long dormitoryId;
    private String dormitoryName;
    private String dormitoryType;
    private String name;
    private BigDecimal area;
    private Integer capacity;
    /** 部屋個別寮費（円、nullは共通設定を使用） */
    private Integer rent;
    /** ステータス: available / occupied / maintenance */
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    /** 現在の在居人数 */
    private Integer residentCount;
}
