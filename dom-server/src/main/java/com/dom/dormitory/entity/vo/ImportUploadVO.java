package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.util.List;

/** インポートアップロードVO */
@Data
public class ImportUploadVO {
    private String fileId;
    private String fileName;
    private Integer totalRows;
    /** Excelから読み取った列名リスト */
    private List<String> headers;
}
