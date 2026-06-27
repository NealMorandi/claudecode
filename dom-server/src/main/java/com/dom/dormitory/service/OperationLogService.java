package com.dom.dormitory.service;

import com.dom.dormitory.entity.vo.OperationLogVO;
import com.dom.dormitory.entity.vo.PageVO;
import jakarta.servlet.http.HttpServletResponse;

public interface OperationLogService {
    PageVO<OperationLogVO> list(String keyword, String action, String from, String to, Integer page, Integer pageSize);
    void export(String keyword, String action, String from, String to, HttpServletResponse response);
    /** 操作ログ記録（ServiceImplから呼び出す） */
    void record(Long operatorId, String operatorName, String action, String targetDesc, String targetId, String detail);
}
