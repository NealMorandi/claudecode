package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.Residence;
import com.dom.dormitory.entity.vo.ResidenceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ResidenceMapper {
    Residence selectById(@Param("id") Long id);
    /** 社員の現在有効な入居情報を取得 */
    Residence selectActiveByEmployeeId(@Param("employeeId") Long employeeId);
    List<ResidenceVO> selectList(@Param("keyword") String keyword, @Param("status") String status, @Param("checkinFrom") String checkinFrom, @Param("checkinTo") String checkinTo, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    Long countList(@Param("keyword") String keyword, @Param("status") String status, @Param("checkinFrom") String checkinFrom, @Param("checkinTo") String checkinTo);
    void insert(Residence residence);
    void update(Residence residence);
    /** 対象年月に在居中の入居レコード取得（寮費計算用） */
    List<Residence> selectActiveInMonth(@Param("yearMonth") String yearMonth);
    /** 長期利用中の入居レコード取得 */
    List<Residence> selectLongTermResidences(@Param("thresholdYears") Integer thresholdYears);
    /** 現在在居中の全入居レコード取得 */
    List<Residence> selectAllActive();
    /** 部屋の最終退寮日取得（YYYY-MM-DD、なければnull） */
    String selectLastCheckoutByRoomId(@Param("roomId") Long roomId);
    /** 部屋の現在在居数取得 */
    Integer countActiveByRoomId(@Param("roomId") Long roomId);
    /** 寮の現在在居数取得（削除前チェック用） */
    Long countActiveByDormitoryId(@Param("dormitoryId") Long dormitoryId);
    /** 寮内の全部屋ごとの在居数を一括取得（roomId → residentCount） */
    List<java.util.Map<String, Object>> selectResidentCountsByDormitoryId(@Param("dormitoryId") Long dormitoryId);
    /** 現在入居中の人数合計（ダッシュボード用） */
    Long countActive();
}
