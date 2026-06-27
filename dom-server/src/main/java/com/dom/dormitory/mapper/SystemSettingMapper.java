package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.SystemSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemSettingMapper {
    SystemSetting selectAll();
    void updateAll(@Param("longTermYears") Integer longTermYears,
                   @Param("unitPriceJapan") Integer unitPriceJapan,
                   @Param("unitPriceChina") Integer unitPriceChina);
}
