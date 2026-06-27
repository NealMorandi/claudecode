package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 社員情報VO */
@Data
public class EmployeeVO {
    private Long id;
    private String employeeNo;
    private String name;
    /** 性別: 男 / 女 */
    private String gender;
    /** 区分: japan / china */
    private String type;
    private String department;
    private LocalDate firstUseDate;
    /** 現在の入居ステータス */
    private String residenceStatus;
    private LocalDateTime createdAt;
}
