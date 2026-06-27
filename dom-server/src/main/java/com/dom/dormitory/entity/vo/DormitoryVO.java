package com.dom.dormitory.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/** 寮情報VO */
@Data
public class DormitoryVO {
    private Long id;
    private String name;
    private String type;
    /** 状態: active=有効, inactive=退却 */
    private String status;
    /** 都道府県 */
    private String prefecture;
    private String address;
    private String layout;
    private String remark;
    private Integer totalRooms;
    private Integer availableRooms;
    /** 入居人数 */
    private Integer residentCount;
    /** 入居可能人数（定員合計 − 入居人数） */
    private Integer availableCapacity;
    /** 定員合計 */
    private Integer totalCapacity;
    private LocalDateTime createdAt;
    /** 部屋別寮費リスト */
    private List<RoomRentVO> roomRents;

    @Data
    public static class RoomRentVO {
        private String name;
        private Integer rent;
    }
}
