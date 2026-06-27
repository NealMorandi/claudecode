package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 寮マスタエンティティ
 */
@Data
public class Dormitory {
    private Long id;
    private String name;
    /** 種別: 男 / 女 / mixed */
    private String type;
    /** 状態: active=有効, inactive=退却 */
    private String status;
    /** 都道府県 */
    private String prefecture;
    private String address;
    private String layout;
    private String remark;
    /** 論理削除: 0=有効, 1=削除 */
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
