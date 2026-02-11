package com.example.spba.service;

import com.example.spba.domain.dto.EnterpriseSubmitDTO;
import com.example.spba.domain.dto.EnterpriseUpdateDTO;
import com.example.spba.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EnterpriseApplyService {

    R submitApplication(EnterpriseSubmitDTO submitDTO, String userId, String userName);

    R queryViewApplications();
    
    R withdrawApplication(String applicationId, String userId);
    
    R updateApplication(EnterpriseUpdateDTO submitDTO, String userId, String userName);

}
