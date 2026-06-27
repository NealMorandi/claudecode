package com.dom.dormitory.service;

import com.dom.dormitory.entity.dto.ImportExecuteDTO;
import com.dom.dormitory.entity.dto.ImportValidateDTO;
import com.dom.dormitory.entity.vo.ImportExecuteVO;
import com.dom.dormitory.entity.vo.ImportUploadVO;
import com.dom.dormitory.entity.vo.ImportValidateVO;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    ImportUploadVO upload(MultipartFile file, String type);
    ImportValidateVO validate(ImportValidateDTO dto);
    ImportExecuteVO execute(ImportExecuteDTO dto);
    ImportExecuteVO getReport(String jobId);
}
