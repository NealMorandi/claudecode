package com.dom.dormitory.entity.dto;

import lombok.Data;
import java.util.Map;

/** インポート検証DTO */
@Data
public class ImportValidateDTO {
    private String fileId;
    /** Excel列→システムフィールドのマッピング */
    private Map<String, String> mapping;
    /** インポート種別: residence / vacancy / rent / equipment */
    private String type;
}
