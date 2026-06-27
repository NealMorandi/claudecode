package com.dom.dormitory.service;

import com.dom.dormitory.entity.vo.VacancyVO;
import java.util.List;

public interface VacancyService {
    List<VacancyVO> list(String gender, String date);
}
