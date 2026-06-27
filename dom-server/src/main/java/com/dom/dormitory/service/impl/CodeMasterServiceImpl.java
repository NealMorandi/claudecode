package com.dom.dormitory.service.impl;

import com.dom.dormitory.entity.CodeMaster;
import com.dom.dormitory.entity.vo.CodeMasterVO;
import com.dom.dormitory.mapper.CodeMasterMapper;
import com.dom.dormitory.service.CodeMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** コードマスタサービス実装 */
@Service
@RequiredArgsConstructor
public class CodeMasterServiceImpl implements CodeMasterService {

    private final CodeMasterMapper codeMasterMapper;

    @Override
    public List<CodeMasterVO> listByCategory(String category) {
        List<CodeMaster> list = codeMasterMapper.selectByCategory(category);
        return list.stream().map(m -> {
            CodeMasterVO vo = new CodeMasterVO();
            vo.setCode(m.getCode());
            vo.setName(m.getName());
            vo.setSortOrder(m.getSortOrder());
            return vo;
        }).collect(Collectors.toList());
    }
}
