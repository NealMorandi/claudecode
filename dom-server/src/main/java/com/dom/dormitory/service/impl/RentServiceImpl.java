package com.dom.dormitory.service.impl;

import com.dom.dormitory.constant.MessageConstant;
import com.dom.dormitory.entity.*;
import com.dom.dormitory.entity.dto.RentCalculateDTO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.RentCalculateVO;
import com.dom.dormitory.entity.vo.RentHistoryVO;
import com.dom.dormitory.exception.BusinessException;
import com.dom.dormitory.mapper.*;
import com.dom.dormitory.service.RentService;
import com.dom.dormitory.service.SettingsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** 寮費サービス実装 */
@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService {

    private final RentMapper rentMapper;
    private final ResidenceMapper residenceMapper;
    private final EmployeeMapper employeeMapper;
    private final RoomMapper roomMapper;
    private final DormitoryMapper dormitoryMapper;
    private final SystemSettingMapper systemSettingMapper;

    @Override
    public RentCalculateVO calculate(RentCalculateDTO dto) {
        String yearMonth = dto.getYearMonth();
        YearMonth ym = YearMonth.parse(yearMonth);
        int monthDays = ym.lengthOfMonth();

        // システム設定から単価を取得
        SystemSetting settings = systemSettingMapper.selectAll();
        BigDecimal unitPriceJapan = settings != null && settings.getUnitPriceJapan() != null
                ? settings.getUnitPriceJapan() : new BigDecimal("2000");
        BigDecimal unitPriceChina = settings != null && settings.getUnitPriceChina() != null
                ? settings.getUnitPriceChina() : BigDecimal.ZERO;

        List<Residence> residences = residenceMapper.selectActiveInMonth(yearMonth);
        List<RentCalculateVO.RentItemVO> items = new ArrayList<>();

        for (Residence residence : residences) {
            Employee employee = employeeMapper.selectById(residence.getEmployeeId());
            Room room = roomMapper.selectById(residence.getRoomId());
            Dormitory dormitory = room != null ? dormitoryMapper.selectById(room.getDormitoryId()) : null;
            if (employee == null || room == null) {
                continue;
            }

            // 対象月の実入居日数計算
            // 当月計算の場合は計算実行日（本日）を上限とし、過去月は月末を上限とする
            LocalDate today = LocalDate.now();
            LocalDate monthStart = ym.atDay(1);
            LocalDate monthEnd = YearMonth.from(today).equals(ym) ? today : ym.atEndOfMonth();
            LocalDate checkin = residence.getCheckinDate();
            LocalDate checkout = residence.getCheckoutDate() != null ? residence.getCheckoutDate() : monthEnd;
            LocalDate from = checkin.isAfter(monthStart) ? checkin : monthStart;
            LocalDate to = checkout.isBefore(monthEnd) ? checkout : monthEnd;
            int days = (int) (to.toEpochDay() - from.toEpochDay() + 1);
            if (days <= 0) {
                continue;
            }

            BigDecimal unitPrice = "japan".equals(employee.getType()) ? unitPriceJapan : unitPriceChina;
            // 寮費 = 単価（部屋あたり日額） × 在居日数
            BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(days));

            RentCalculateVO.RentItemVO item = new RentCalculateVO.RentItemVO();
            item.setEmployeeId(employee.getId());
            item.setEmployeeName(employee.getName());
            item.setEmployeeNo(employee.getEmployeeNo());
            item.setEmployeeType(employee.getType());
            item.setRoomName(room.getName());
            item.setDormitoryName(dormitory != null ? dormitory.getName() : "");
            item.setArea(room.getArea());
            item.setDays(days);
            item.setUnitPrice(unitPrice);
            item.setMonthDays(monthDays);
            item.setAmount(amount);
            items.add(item);
        }

        BigDecimal totalAmount = items.stream()
                .map(RentCalculateVO.RentItemVO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        RentCalculateVO vo = new RentCalculateVO();
        vo.setYearMonth(yearMonth);
        vo.setTotalCount(items.size());
        vo.setTotalAmount(totalAmount);
        vo.setItems(items);
        return vo;
    }

    @Override
    @Transactional
    public void confirm(RentCalculateDTO dto) {
        Long existing = rentMapper.countByYearMonth(dto.getYearMonth());
        if (existing != null && existing > 0) {
            throw new BusinessException(MessageConstant.RENT_ALREADY_CONFIRMED);
        }
        RentCalculateVO preview = calculate(dto);
        if (preview.getItems().isEmpty()) {
            return;
        }
        // 計算結果をDBに保存して確定
        List<Rent> rentList = preview.getItems().stream().map(item -> {
            Rent rent = new Rent();
            // 入居IDの取得
            Residence res = residenceMapper.selectActiveByEmployeeId(item.getEmployeeId());
            if (res == null) {
                // 退寮済みの場合、対象月の入居レコードを再取得
                List<Residence> allInMonth = residenceMapper.selectActiveInMonth(dto.getYearMonth());
                res = allInMonth.stream()
                        .filter(r -> r.getEmployeeId().equals(item.getEmployeeId()))
                        .findFirst().orElse(null);
            }
            rent.setEmployeeId(item.getEmployeeId());
            rent.setResidenceId(res != null ? res.getId() : null);
            rent.setYearMonth(dto.getYearMonth());
            rent.setDays(item.getDays());
            rent.setArea(item.getArea());
            rent.setUnitPrice(item.getUnitPrice());
            rent.setMonthDays(item.getMonthDays());
            rent.setAmount(item.getAmount());
            return rent;
        }).collect(Collectors.toList());

        rentMapper.batchInsert(rentList);
        rentMapper.confirmByYearMonth(dto.getYearMonth());
    }

    @Override
    public PageVO<RentHistoryVO> history(Long employeeId, String yearMonth, String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Rent> list = rentMapper.selectHistory(employeeId, yearMonth, status, offset, pageSize);
        Long total = rentMapper.countHistory(employeeId, yearMonth, status);
        Long confirmedCount = rentMapper.countConfirmedHistory(employeeId, yearMonth);
        java.math.BigDecimal totalAmount = rentMapper.sumHistoryAmount(employeeId, yearMonth);
        List<RentHistoryVO> voList = list.stream().map(r -> toHistoryVO(r)).collect(Collectors.toList());
        PageVO<RentHistoryVO> pageVO = new PageVO<>(voList, total);
        pageVO.setConfirmedCount(confirmedCount);
        pageVO.setTotalAmount(totalAmount != null ? totalAmount : java.math.BigDecimal.ZERO);
        return pageVO;
    }

    @Override
    public RentHistoryVO getById(Long id) {
        Rent rent = rentMapper.selectById(id);
        if (rent == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        return toHistoryVO(rent);
    }

    @Override
    @Transactional
    public void cancelByYearMonth(String yearMonth) {
        Long count = rentMapper.countByYearMonth(yearMonth);
        if (count == null || count == 0) {
            throw new BusinessException("対象月の確定済み寮費が見つかりません");
        }
        rentMapper.deleteByYearMonth(yearMonth);
    }

    @Override
    public void export(String keyword, String yearMonth, HttpServletResponse response) {
        List<Rent> list = rentMapper.selectForExport(keyword, yearMonth);
        String[] headers = {"ID", "社員番号", "社員名", "対象年月", "日数", "面積", "単価", "月日数", "寮費", "確定"};

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("寮費履歴");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            int rowNum = 1;
            for (Rent r : list) {
                Row row = sheet.createRow(rowNum++);
                Employee employee = employeeMapper.selectById(r.getEmployeeId());
                row.createCell(0).setCellValue(r.getId() != null ? r.getId().doubleValue() : 0);
                row.createCell(1).setCellValue(employee != null ? employee.getEmployeeNo() : "");
                row.createCell(2).setCellValue(employee != null ? employee.getName() : "");
                row.createCell(3).setCellValue(r.getYearMonth());
                row.createCell(4).setCellValue(r.getDays() != null ? r.getDays() : 0);
                row.createCell(5).setCellValue(r.getArea() != null ? r.getArea().doubleValue() : 0);
                row.createCell(6).setCellValue(r.getUnitPrice() != null ? r.getUnitPrice().doubleValue() : 0);
                row.createCell(7).setCellValue(r.getMonthDays() != null ? r.getMonthDays() : 0);
                row.createCell(8).setCellValue(r.getAmount() != null ? r.getAmount().doubleValue() : 0);
                row.createCell(9).setCellValue("confirmed".equals(r.getStatus()) ? "確定" : "未確定");
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=rent_history.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException(MessageConstant.SERVER_ERROR);
        }
    }

    private RentHistoryVO toHistoryVO(Rent r) {
        RentHistoryVO vo = new RentHistoryVO();
        vo.setId(r.getId());
        vo.setEmployeeId(r.getEmployeeId());
        vo.setYearMonth(r.getYearMonth());
        vo.setDays(r.getDays());
        vo.setArea(r.getArea());
        vo.setUnitPrice(r.getUnitPrice());
        vo.setMonthDays(r.getMonthDays());
        vo.setAmount(r.getAmount());
        vo.setStatus(r.getStatus());
        vo.setConfirmedAt(r.getConfirmedAt());
        Employee employee = employeeMapper.selectById(r.getEmployeeId());
        if (employee != null) {
            vo.setEmployeeName(employee.getName());
            vo.setEmployeeNo(employee.getEmployeeNo());
        }
        if (r.getResidenceId() != null) {
            Residence residence = residenceMapper.selectById(r.getResidenceId());
            if (residence != null) {
                Room room = roomMapper.selectById(residence.getRoomId());
                if (room != null) {
                    vo.setRoomName(room.getName());
                    Dormitory dormitory = dormitoryMapper.selectById(room.getDormitoryId());
                    if (dormitory != null) {
                        vo.setDormitoryName(dormitory.getName());
                    }
                }
            }
        }
        return vo;
    }
}
