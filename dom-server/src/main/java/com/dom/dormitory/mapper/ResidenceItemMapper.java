package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.ResidenceItem;
import com.dom.dormitory.entity.vo.ResidenceItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ResidenceItemMapper {
    ResidenceItem selectById(@Param("id") Long id);
    List<ResidenceItem> selectByResidenceId(@Param("residenceId") Long residenceId);
    /** 備品名/カテゴリをJOINで一括取得（N+1回避） */
    List<ResidenceItemVO> selectWithEquipmentByResidenceId(@Param("residenceId") Long residenceId);
    /** 倉庫保管中の備品一覧（退寮後、disposition=storage） */
    List<ResidenceItem> selectStorageItems();
    void insert(ResidenceItem residenceItem);
    void batchInsert(@Param("list") List<ResidenceItem> list);
    void update(ResidenceItem residenceItem);
    void batchUpdate(@Param("list") List<ResidenceItem> list);
}
