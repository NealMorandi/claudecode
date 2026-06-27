package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 備品履歴エンティティ */
@Data
public class EquipmentHistory {
    private Long id;
    private Long equipmentId;
    /** 操作種別: created / assigned / returned / moved / storage / disposed / reused */
    private String action;
    private Long fromRoomId;
    private Long toRoomId;
    private String storageLocation;
    private Integer quantity;
    private String equipmentCondition;
    private Long residenceId;
    private Long employeeId;
    private Long operatorId;
    private String note;
    private LocalDateTime actionAt;
}
