package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

/** 操作ログVO */
@Data
public class OperationLogVO {
    private Long id;
    private Long operatorId;
    private String operatorName;
    private String action;
    private String targetDesc;
    private String targetId;
    private String detail;
    private LocalDateTime createdAt;
}
