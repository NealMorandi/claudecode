package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 備品保管登録DTO */
@Data
public class StorageRegisterDTO {
    private Long equipmentId;
    private Long roomId;
    private Long employeeId;
    private String equipmentCondition;
    private Integer quantity;
    private String storageLocation;
    private String remark;
}
