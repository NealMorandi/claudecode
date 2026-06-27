package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.util.List;

/** 寮別稼働率VO */
@Data
public class DormitoryOccupancyVO {
    private Long id;
    private String name;
    private Integer totalRooms;
    private Integer occupiedRooms;
    private Integer availableRooms;
    /** 稼働率 0.0〜1.0 */
    private Double occupancyRate;
    /** 入居人数（active residences 合計） */
    private Integer residentCount;
    /** 入居可能人数（定員合計 − 入居人数） */
    private Integer availableCapacity;
    /** 定員合計（全部屋の capacity 合計） */
    private Integer totalCapacity;
}
