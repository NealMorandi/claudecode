package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.math.BigDecimal;

/** 部屋別在居状況VO（寮詳細画面用） */
@Data
public class RoomResidentVO {
    private Long       roomId;
    private String     roomName;
    private Integer    capacity;
    private Integer    residentCount;
    /** 入居中でない場合 null */
    private Long       residenceId;
    private Long       employeeId;
    private String     employeeName;
    private String     employeeType;
    private String     department;
    private String     checkinDate;
    private String     checkoutDate;
    /** 当月寮費（入居者がいる場合のみ設定） */
    private BigDecimal currentMonthRent;
    /** 当月の計算対象日数 */
    private Integer    currentMonthDays;
}
