package com.dom.dormitory.entity.dto;

import lombok.Data;

/** 入居登録DTO */
@Data
public class ResidenceDTO {
    private Long employeeId;
    private String checkinDate;
    private String expectedCheckoutDate;
    private Long roomId;
}
