package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 社員マスタエンティティ
 */
@Data
public class Employee {
    private Long id;
    private String employeeNo;
    private String name;
    /** 性別: 男 / 女 */
    private String gender;
    /** 社員区分: japan / china */
    private String type;
    private String department;
    /** 初回入居日（入居登録時に自動設定） */
    private LocalDate firstUseDate;
    /** 論理削除: 0=有効, 1=削除 */
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
