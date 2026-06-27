package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.LongTermAlertStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface LongTermAlertMapper {
    LongTermAlertStatus selectByEmployeeId(@Param("employeeId") Long employeeId);
    List<LongTermAlertStatus> selectAll();
    void insertOrUpdate(LongTermAlertStatus status);
    Long countByStatus(@Param("alertStatus") String alertStatus);
    /** 長期利用警告一覧をJOINで1クエリ取得（N+1回避） */
    List<com.dom.dormitory.entity.vo.LongTermAlertVO> selectAlertList(@Param("thresholdYears") int thresholdYears);
}
