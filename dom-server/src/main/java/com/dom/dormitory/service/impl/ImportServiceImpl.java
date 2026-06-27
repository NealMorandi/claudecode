package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.constant.StatusConstant;
import com.dom.dormitory.entity.vo.ImportExecuteVO;
import com.dom.dormitory.entity.vo.ImportUploadVO;
import com.dom.dormitory.entity.vo.ImportValidateVO;
import com.dom.dormitory.entity.dto.ImportExecuteDTO;
import com.dom.dormitory.entity.dto.ImportValidateDTO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.service.ImportService;
import com.dom.dormitory.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** インポートサービス実装（簡易版） */
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    @Override
    public ImportUploadVO upload(MultipartFile file, String type) {
        if (file.isEmpty()) {
            throw new BusinessException(MessageConstant.PARAM_INVALID);
        }
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new BusinessException(MessageConstant.PARAM_INVALID);
            }

            List<String> headers = new ArrayList<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell != null ? cell.toString() : "");
            }
            int totalRows = Math.max(0, sheet.getLastRowNum());

            // ファイル情報をRedisに一時保存（30分）
            String fileId = UUID.randomUUID().toString();
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", file.getOriginalFilename());
            fileInfo.put("totalRows", totalRows);
            fileInfo.put("headers", headers);
            fileInfo.put("type", type);
            redisUtil.set(StatusConstant.REDIS_IMPORT_PREFIX + fileId,
                    objectMapper.writeValueAsString(fileInfo), 1800L, TimeUnit.SECONDS);

            ImportUploadVO vo = new ImportUploadVO();
            vo.setFileId(fileId);
            vo.setFileName(file.getOriginalFilename());
            vo.setTotalRows(totalRows);
            vo.setHeaders(headers);
            return vo;
        } catch (IOException e) {
            throw new BusinessException(MessageConstant.PARAM_INVALID);
        }
    }

    @Override
    public ImportValidateVO validate(ImportValidateDTO dto) {
        // 簡易検証：現時点では成功を返す
        ImportValidateVO vo = new ImportValidateVO();
        vo.setTotalRows(0);
        vo.setValidRows(0);
        vo.setErrorRows(0);
        vo.setErrors(new ArrayList<>());
        return vo;
    }

    @Override
    public ImportExecuteVO execute(ImportExecuteDTO dto) {
        // 非同期実行をシミュレート（実際のインポートは業務要件に応じて実装）
        String jobId = UUID.randomUUID().toString();
        ImportExecuteVO vo = new ImportExecuteVO();
        vo.setJobId(jobId);
        vo.setStatus("completed");
        vo.setSuccessCount(0);
        vo.setErrorCount(0);
        // ジョブ結果をRedisに保存
        try {
            redisUtil.set(StatusConstant.REDIS_IMPORT_PREFIX + "job:" + jobId,
                    objectMapper.writeValueAsString(vo), 3600L, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new BusinessException(MessageConstant.SERVER_ERROR);
        }
        return vo;
    }

    @Override
    public ImportExecuteVO getReport(String jobId) {
        String json = redisUtil.get(StatusConstant.REDIS_IMPORT_PREFIX + "job:" + jobId);
        if (json == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        try {
            return objectMapper.readValue(json, ImportExecuteVO.class);
        } catch (Exception e) {
            throw new BusinessException(MessageConstant.SERVER_ERROR);
        }
    }
}
