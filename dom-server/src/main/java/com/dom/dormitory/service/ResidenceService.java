package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.CheckoutDTO;
import com.dom.dormitory.entity.dto.ResidenceDTO;
import com.dom.dormitory.entity.dto.ResidenceUpdateDTO;
import com.dom.dormitory.entity.vo.PageVO;
import com.dom.dormitory.entity.vo.ResidenceDetailVO;
import com.dom.dormitory.entity.vo.ResidenceVO;

public interface ResidenceService {
    PageVO<ResidenceVO> list(String keyword, String status, String checkinFrom, String checkinTo, Integer page, Integer pageSize);
    ResidenceDetailVO getById(Long id);
    ResidenceVO create(ResidenceDTO dto);
    ResidenceVO update(Long id, ResidenceUpdateDTO dto);
    void checkout(Long id, CheckoutDTO dto);
}
