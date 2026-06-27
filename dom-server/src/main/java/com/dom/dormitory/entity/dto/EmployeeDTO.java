package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 社員登録・更新DTO */
@Data
public class EmployeeDTO {
    private String employeeNo;
    private String name;
    /** 性別: 男 / 女 */
    private String gender;
    /** 社員区分: japan / china */
    private String type;
    private String department;
}
