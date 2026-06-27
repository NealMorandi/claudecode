package com.dom.dormitory.entity.vo;

import lombok.Data;

/** ダッシュボードサマリーVO */
@Data
public class DashboardSummaryVO {
    /** 総社員数 */
    private Integer totalEmployees;
    /** 入居中社員数 */
    private Integer occupiedEmployees;
    /** 総室数 */
    private Integer totalRooms;
    /** 空室数 */
    private Integer availableRooms;
    /** 警告件数 */
    private Integer alertCount;
    /** 当月確定寮費合計 */
    private java.math.BigDecimal monthlyRent;
    /** 当月寮費計算状態: uncalculated / confirmed */
    private String rentStatus;
    /** 当月確定済み寮費レコード件数 */
    private Integer rentCount;
}
