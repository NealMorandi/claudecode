package com.dom.dormitory.service.impl;

import com.dom.dormitory.entity.vo.VacancyVO;
import com.dom.dormitory.mapper.RoomMapper;
import com.dom.dormitory.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/** 空室サービス実装 */
@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final RoomMapper roomMapper;

    @Override
    public List<VacancyVO> list(String gender, String date) {
        return roomMapper.selectVacancyList(date, gender);
    }
}
