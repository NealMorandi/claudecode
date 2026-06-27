package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.SettingsDTO;
import com.dom.dormitory.entity.vo.SettingsVO;

public interface SettingsService {
    SettingsVO get();
    SettingsVO update(SettingsDTO dto);
}
