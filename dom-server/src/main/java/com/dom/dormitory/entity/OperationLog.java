package com.dom.dormitory.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 操作ログエンティティ */
@Data
public class OperationLog {
    private Long id;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String targetId;
    private String targetDesc;
    private String detail;
    private LocalDateTime createdAt;
}
