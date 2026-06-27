package com.dom.dormitory.entity.vo;

import lombok.Data;

/** 倉庫備品VO */
@Data
public class StorageVO {
    private Long historyId;
    private Long residenceId;
    private Long equipmentId;
    private String equipmentName;
    private String maker;
    private String category;
    private String equipmentCondition;
    private String storageLocation;
    private Long roomId;
    private Integer quantity;
    private Long employeeId;
    private String employeeName;
    private String roomName;
    private String dormitoryName;
    private String checkoutDate;
    private String remark;
}
