package com.dom.dormitory.entity.dto;

import lombok.Data;

/** インポート実行DTO */
@Data
public class ImportExecuteDTO {
    private String fileId;
    /** インポート種別 */
    private String type;
    /** エラー行をスキップするか */
    private Boolean skipErrors;
}
