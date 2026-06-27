package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 寮登録・更新DTO */
@Data
public class DormitoryDTO {
    private String name;
    /** 種別: 男 / 女 / mixed */
    private String type;
    /** 都道府県 */
    private String prefecture;
    private String address;
    private String layout;
    private String remark;
}
