package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.util.List;

/** インポート検証結果VO */
@Data
public class ImportValidateVO {
    private Integer totalRows;
    private Integer validRows;
    private Integer errorRows;
    private List<ErrorDetail> errors;

    @Data
    public static class ErrorDetail {
        private Integer row;
        private String column;
        private String message;
    }
}
