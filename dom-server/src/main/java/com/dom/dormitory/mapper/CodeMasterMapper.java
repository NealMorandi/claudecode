package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.CodeMaster;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CodeMasterMapper {
    /** カテゴリ別コード一覧取得（表示順昇順） */
    List<CodeMaster> selectByCategory(@Param("category") String category);
}
