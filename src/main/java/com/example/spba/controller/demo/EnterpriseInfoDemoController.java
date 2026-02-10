package com.example.spba.controller.demo;

import com.example.spba.controller.base.BaseController;
import com.example.spba.service.BusinessEnterpriseService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 企业信息查询演示Controller
 */
@RestController
@RequestMapping("/demo/enterprise")
public class EnterpriseInfoDemoController extends BaseController {

    @Autowired
    private BusinessEnterpriseService businessEnterpriseService;

    /**
     * 演示修改后的企业信息查询接口
     * @return 包含tag和officeAddress信息的企业信息
     */
    @PostMapping("/info-with-tags-addresses")
    public R getEnterpriseInfoWithTagsAndAddresses() {
        try {
            String enterpriseId = getCurrentUserId();
            if (enterpriseId == null) {
                return R.error("未获取到企业ID，请先登录");
            }
            
            Object enterpriseInfo = businessEnterpriseService.getEnterpriseInfoWithApprovalStatus(enterpriseId);
            return R.success(enterpriseInfo);
        } catch (Exception e) {
            return R.error("查询企业信息失败: " + e.getMessage());
        }
    }
}