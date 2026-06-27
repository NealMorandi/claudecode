package com.dom.dormitory.service;

import com.dom.dormitory.entity.vo.CodeMasterVO;
import java.util.List;

/** コードマスタサービス */
public interface CodeMasterService {
    List<CodeMasterVO> listByCategory(String category);
}
