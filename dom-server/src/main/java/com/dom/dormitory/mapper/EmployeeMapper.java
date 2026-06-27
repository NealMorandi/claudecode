package com.dom.dormitory.mapper;

import com.dom.dormitory.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EmployeeMapper {
    Employee selectById(@Param("id") Long id);
    List<Employee> selectList(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    Long countList(@Param("keyword") String keyword);
    List<Employee> selectByKeyword(@Param("keyword") String keyword);
    void insert(Employee employee);
    void update(Employee employee);
    void deleteById(@Param("id") Long id);
    /** 社員の最初の入居日更新 */
    void updateFirstUseDate(@Param("id") Long id, @Param("firstUseDate") java.time.LocalDate firstUseDate);
    Long countAll();
    Long countResiding();
    /** 区分別カウント（サマリーカード用） */
    Long countByType(@Param("type") String type);
    /** 社員一覧＋在住状況をJOINで1クエリ取得（N+1回避） */
    List<com.dom.dormitory.entity.vo.EmployeeVO> selectListWithStatus(
            @Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    /** ドロップダウン検索＋在住状況をJOINで1クエリ取得 */
    List<com.dom.dormitory.entity.vo.EmployeeVO> selectByKeywordWithStatus(@Param("keyword") String keyword);
}
