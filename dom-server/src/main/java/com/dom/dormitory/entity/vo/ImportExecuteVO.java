package com.dom.dormitory.entity.vo;

import lombok.Data;

/** インポート実行結果VO */
@Data
public class ImportExecuteVO {
    private String jobId;
    /** ジョブステータス: pending / running / completed / failed */
    private String status;
    private Integer successCount;
    private Integer errorCount;
}
