package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.*;
import com.dom.dormitory.entity.dto.LongTermAlertStatusDTO;
import com.dom.dormitory.entity.vo.LongTermAlertVO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.*;
import com.dom.dormitory.service.LongTermAlertService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** 長期利用警告サービス実装 */
@Service
@RequiredArgsConstructor
public class LongTermAlertServiceImpl implements LongTermAlertService {

    private final EmployeeMapper employeeMapper;
    private final LongTermAlertMapper longTermAlertMapper;
    private final SystemSettingMapper systemSettingMapper;

    @Override
    public PageVO<LongTermAlertVO> list(Integer minYears, String status, Integer page, Integer pageSize) {
        List<LongTermAlertVO> all = buildAlertList();
        if (minYears != null) {
            all = all.stream().filter(v -> v.getYearsOfStay() != null && v.getYearsOfStay() >= minYears).collect(Collectors.toList());
        }
        if (status != null && !status.isEmpty()) {
            all = all.stream().filter(v -> status.equals(v.getAlertStatus())).collect(Collectors.toList());
        }
        long pendingCount = all.stream().filter(v -> "pending".equals(v.getAlertStatus())).count();
        // notified / applied / done をすべて「対応済み」としてカウント
        long doneCount = all.stream().filter(v -> !"pending".equals(v.getAlertStatus())).count();
        int total = all.size();
        int offset = (page - 1) * pageSize;
        int end = Math.min(offset + pageSize, total);
        List<LongTermAlertVO> paged = offset >= total ? new ArrayList<>() : all.subList(offset, end);
        PageVO<LongTermAlertVO> pageVO = new PageVO<>(paged, (long) total);
        pageVO.setPendingCount(pendingCount);
        pageVO.setDoneCount(doneCount);
        return pageVO;
    }

    @Override
    public void updateStatus(Long employeeId, LongTermAlertStatusDTO dto) {
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        LongTermAlertStatus status = new LongTermAlertStatus();
        status.setEmployeeId(employeeId);
        status.setAlertStatus(dto.getAlertStatus());
        longTermAlertMapper.insertOrUpdate(status);
    }

    @Override
    public void export(HttpServletResponse response, Integer minYears, String status) {
        List<LongTermAlertVO> list = buildAlertList();
        if (minYears != null) {
            list = list.stream().filter(v -> v.getYearsOfStay() != null && v.getYearsOfStay() >= minYears).collect(Collectors.toList());
        }
        if (status != null && !status.isEmpty()) {
            list = list.stream().filter(v -> status.equals(v.getAlertStatus())).collect(Collectors.toList());
        }
        String[] headers = {"社員番号", "社員名", "部署", "部屋名", "寮名", "入居日", "在居年数", "警告ステータス"};

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("長期利用警告");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int rowNum = 1;
            for (LongTermAlertVO vo : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(vo.getEmployeeNo() != null ? vo.getEmployeeNo() : "");
                row.createCell(1).setCellValue(vo.getEmployeeName() != null ? vo.getEmployeeName() : "");
                row.createCell(2).setCellValue(vo.getDepartment() != null ? vo.getDepartment() : "");
                row.createCell(3).setCellValue(vo.getRoomName() != null ? vo.getRoomName() : "");
                row.createCell(4).setCellValue(vo.getDormitoryName() != null ? vo.getDormitoryName() : "");
                row.createCell(5).setCellValue(vo.getCheckinDate() != null ? vo.getCheckinDate().toString() : "");
                row.createCell(6).setCellValue(vo.getYearsOfStay() != null ? vo.getYearsOfStay() : 0);
                row.createCell(7).setCellValue(vo.getAlertStatus() != null ? vo.getAlertStatus() : "pending");
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=long_term_alerts.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException(MessageConstant.SERVER_ERROR);
        }
    }

    @Override
    public List<LongTermAlertVO> getTopAlerts(Integer limit) {
        List<LongTermAlertVO> all = buildAlertList();
        if (limit == Integer.MAX_VALUE) {
            return all;
        }
        return all.stream().limit(limit).collect(Collectors.toList());
    }

    private List<LongTermAlertVO> buildAlertList() {
        SystemSetting setting = systemSettingMapper.selectAll();
        int thresholdYears = setting != null && setting.getLongTermYears() != null ? setting.getLongTermYears() : 3;
        return longTermAlertMapper.selectAlertList(thresholdYears);
    }
}
