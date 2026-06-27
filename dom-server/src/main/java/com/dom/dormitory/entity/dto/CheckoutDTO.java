package com.dom.dormitory.entity.dto;

import lombok.Data;
import java.util.List;

/** 退寮処理DTO */
@Data
public class CheckoutDTO {
    private String checkoutDate;
    /** 退寮理由: relocation / retirement / trip_end / other */
    private String checkoutReason;
    private List<CheckoutItemDTO> items;

    @Data
    public static class CheckoutItemDTO {
        private Long id;
        /** 処分方法: discard / storage / reuse */
        private String disposition;
        private String storageLocation;
    }
}
