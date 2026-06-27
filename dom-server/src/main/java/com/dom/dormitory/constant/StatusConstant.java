package com.dom.dormitory.constant;

/**
 * ステータス定数クラス
 */
public class StatusConstant {

    /** 有効 */
    public static final Integer ACTIVE   = 1;
    /** 無効 */
    public static final Integer INACTIVE = 0;

    /** 論理削除: 有効 */
    public static final Integer NOT_DELETED = 0;
    /** 論理削除: 削除済み */
    public static final Integer DELETED     = 1;

    /** 入居ステータス: 入居中 */
    public static final String RESIDENCE_ACTIVE   = "active";
    /** 入居ステータス: 退寮済み */
    public static final String RESIDENCE_CHECKOUT = "checkout";

    /** 部屋ステータス: 空き */
    public static final String ROOM_VACANT   = "vacant";
    /** 部屋ステータス: 入居中 */
    public static final String ROOM_OCCUPIED = "occupied";

    /** 寮費確定: 未確定 */
    public static final Integer RENT_UNCONFIRMED = 0;
    /** 寮費確定: 確定済み */
    public static final Integer RENT_CONFIRMED   = 1;

    /** 備品準備: 未準備 */
    public static final Integer ITEM_NOT_PREPARED = 0;
    /** 備品準備: 準備済み */
    public static final Integer ITEM_PREPARED     = 1;

    /** 警告ステータス */
    public static final String ALERT_PENDING  = "pending";
    public static final String ALERT_NOTIFIED = "notified";
    public static final String ALERT_APPLIED  = "applied";
    public static final String ALERT_DONE     = "done";

    /** Redis Key プレフィックス */
    public static final String REDIS_KEY_PREFIX    = "dom";
    public static final String REDIS_AUTH_PREFIX   = "dom:auth:token:";
    public static final String REDIS_IMPORT_PREFIX = "dom:import:job:";

    private StatusConstant() {}
}
