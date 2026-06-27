package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.Rent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface RentMapper {
    Rent selectById(@Param("id") Long id);
    List<Rent> selectHistory(@Param("employeeId") Long employeeId, @Param("yearMonth") String yearMonth, @Param("status") String status, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    Long countHistory(@Param("employeeId") Long employeeId, @Param("yearMonth") String yearMonth, @Param("status") String status);
    Long countConfirmedHistory(@Param("employeeId") Long employeeId, @Param("yearMonth") String yearMonth);
    java.math.BigDecimal sumHistoryAmount(@Param("employeeId") Long employeeId, @Param("yearMonth") String yearMonth);
    /** 当月確定済みの寮費合計 */
    BigDecimal sumConfirmedByYearMonth(@Param("yearMonth") String yearMonth);
    /** 当月の確定済み寮費レコード件数 */
    Long countByYearMonth(@Param("yearMonth") String yearMonth);
    void insert(Rent rent);
    void batchInsert(@Param("list") List<Rent> list);
    /** 一括確定 */
    void confirmByYearMonth(@Param("yearMonth") String yearMonth);
    /** 全履歴エクスポート用（フィルタ付き） */
    List<Rent> selectForExport(@Param("keyword") String keyword, @Param("yearMonth") String yearMonth);
    /** 対象月の寮費を論理削除（確定取消） */
    void deleteByYearMonth(@Param("yearMonth") String yearMonth);
}
