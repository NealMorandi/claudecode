package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.Room;
import com.dom.dormitory.entity.vo.RoomVO;
import com.dom.dormitory.entity.vo.VacancyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoomMapper {
    Room selectById(@Param("id") Long id);
    List<Room> selectByDormitoryId(@Param("dormitoryId") Long dormitoryId);
    /** 空き部屋一覧：JOIN で寮名/寮種別を取得（N+1回避） */
    List<RoomVO> selectAvailable(@Param("date") String date, @Param("gender") String gender);
    void insert(Room room);
    void update(Room room);
    void deleteById(@Param("id") Long id);
    /** 部屋ステータス更新 */
    void updateStatus(@Param("id") Long id, @Param("status") String status);
    Long countTotal();
    Long countAvailable();
    /** 寮別の部屋統計 */
    List<java.util.Map<String, Object>> selectOccupancyByDormitory();
    /** 全部屋の名前と寮費を一括取得 */
    List<Room> selectAllNonDeleted();
    /** 空室一覧取得（N+1回避・1クエリで寮名/在居数/最終退寮日を取得） */
    List<VacancyVO> selectVacancyList(@Param("date") String date, @Param("gender") String gender);
    /** 寮別の部屋×在居者フラット一覧（寮詳細画面用） */
    List<java.util.Map<String, Object>> selectRoomResidentsByDormitoryId(@Param("dormitoryId") Long dormitoryId);
}
