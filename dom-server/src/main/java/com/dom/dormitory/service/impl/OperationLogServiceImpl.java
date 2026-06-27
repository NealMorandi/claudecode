package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.OperationLog;
import com.dom.dormitory.entity.vo.OperationLogVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.OperationLogMapper;
import com.dom.dormitory.service.OperationLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/** 操作ログサービス実装 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Override
    public PageVO<OperationLogVO> list(String keyword, String action, String from, String to, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<OperationLog> logs = operationLogMapper.selectList(keyword, action, from, to, offset, pageSize);
        Long total = operationLogMapper.countList(keyword, action, from, to);
        List<OperationLogVO> voList = logs.stream().map(this::toVO).collect(Collectors.toList());
        PageVO<OperationLogVO> pageVO = new PageVO<>(voList, total);
        // サマリーカード用：全データに対してアクション種別集計
        Long createCount  = operationLogMapper.countAllByActionSuffix("create");
        Long updateCount  = operationLogMapper.countAllByActionSuffix("update");
        Long deleteCount  = operationLogMapper.countAllByActionSuffix("delete");
        Long exportCount  = operationLogMapper.countAllByActionSuffix("export");
        pageVO.setPendingCount(createCount + updateCount);   // 作成・更新
        pageVO.setDoneCount(deleteCount);                    // 削除
        pageVO.setConfirmedCount(exportCount);               // エクスポート
        return pageVO;
    }

    @Override
    public void export(String keyword, String action, String from, String to, HttpServletResponse response) {
        List<OperationLog> logs = operationLogMapper.selectForExport(keyword, action, from, to);
        String[] headers = {"ID", "操作者", "アクション", "対象", "詳細", "操作時刻"};

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("操作ログ");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int rowNum = 1;
            for (OperationLog log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(log.getId() != null ? log.getId().doubleValue() : 0);
                row.createCell(1).setCellValue(log.getOperatorName() != null ? log.getOperatorName() : "");
                row.createCell(2).setCellValue(log.getAction() != null ? log.getAction() : "");
                row.createCell(3).setCellValue(log.getTargetDesc() != null ? log.getTargetDesc() : "");
                row.createCell(4).setCellValue(log.getDetail() != null ? log.getDetail() : "");
                row.createCell(5).setCellValue(log.getCreatedAt() != null ? log.getCreatedAt().toString() : "");
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=operation_logs.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException(MessageConstant.SERVER_ERROR);
        }
    }

    @Override
    public void record(Long operatorId, String operatorName, String action, String targetDesc, String targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setAction(action);
        log.setTargetDesc(targetDesc);
        log.setTargetId(targetId);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }

    private OperationLogVO toVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(log.getId());
        vo.setOperatorId(log.getOperatorId());
        vo.setOperatorName(log.getOperatorName());
        vo.setAction(log.getAction());
        vo.setTargetDesc(log.getTargetDesc());
        vo.setTargetId(log.getTargetId());
        vo.setDetail(log.getDetail());
        vo.setCreatedAt(log.getCreatedAt());
        return vo;
    }
}
