package com.dom.dormitory.entity.vo;

import lombok.Data;

/** 備品履歴VO */
@Data
public class EquipmentHistoryVO {
    private Long   id;
    private Long   equipmentId;
    private String equipmentName;
    private String action;
    private String actionLabel;
    private String fromRoomName;
    private String toRoomName;
    private String storageLocation;
    private Long   residenceId;
    private String employeeName;
    private String operatorName;
    private String note;
    private String actionAt;
}
