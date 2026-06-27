package com.dom.dormitory.service.impl;

import com.dom.dormitory.entity.SystemSetting;
import com.dom.dormitory.entity.dto.SettingsDTO;
import com.dom.dormitory.entity.vo.SettingsVO;
import com.dom.dormitory.mapper.SystemSettingMapper;
import com.dom.dormitory.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** システム設定サービス実装 */
@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private final SystemSettingMapper systemSettingMapper;

    @Override
    public SettingsVO get() {
        SystemSetting s = systemSettingMapper.selectAll();
        return toVO(s);
    }

    @Override
    @Transactional
    public SettingsVO update(SettingsDTO dto) {
        systemSettingMapper.updateAll(dto.getLongTermYears(), dto.getUnitPriceJapan(), dto.getUnitPriceChina());
        return get();
    }

    private SettingsVO toVO(SystemSetting s) {
        SettingsVO vo = new SettingsVO();
        if (s != null) {
            vo.setLongTermYears(s.getLongTermYears());
            vo.setUnitPriceJapan(s.getUnitPriceJapan());
            vo.setUnitPriceChina(s.getUnitPriceChina());
            vo.setUpdatedAt(s.getUpdatedAt());
        }
        return vo;
    }
}
