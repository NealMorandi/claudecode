package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperationLogMapper {
    List<OperationLog> selectList(@Param("keyword") String keyword, @Param("action") String action,
                                  @Param("from") String from, @Param("to") String to,
                                  @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    Long countList(@Param("keyword") String keyword, @Param("action") String action,
                   @Param("from") String from, @Param("to") String to);
    void insert(OperationLog log);
    List<OperationLog> selectForExport(@Param("keyword") String keyword, @Param("action") String action,
                                       @Param("from") String from, @Param("to") String to);
    /** action の末尾タイプ別の全件カウント（サマリーカード用） */
    Long countAllByActionSuffix(@Param("type") String type);
}
