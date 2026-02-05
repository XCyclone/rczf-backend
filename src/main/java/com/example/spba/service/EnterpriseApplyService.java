package com.example.spba.service;

import com.example.spba.domain.dto.EnterpriseSubmitDTO;
import com.example.spba.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EnterpriseApplyService {

    R addTag(String tag, String title, List<MultipartFile> files);
    
    R submitApplication(EnterpriseSubmitDTO submitDTO);

    R queryViewApplications();
    
    R withdrawApplication(String applicationId);
}
