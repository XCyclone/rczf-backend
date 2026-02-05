package com.example.spba.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.InfoParametersMapper;
import com.example.spba.dao.BusinessEnterpriseMapper;
import com.example.spba.dao.ProjectInfoMapper;
import com.example.spba.domain.entity.InfoParameters;
import com.example.spba.domain.entity.BusinessEnterprise;
import com.example.spba.domain.entity.ProjectInfo;
import com.example.spba.domain.dto.ProjectInfoDTO;
import com.example.spba.service.CaptchaService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private InfoParametersMapper infoParametersMapper;
    
    @Autowired
    private BusinessEnterpriseMapper businessEnterpriseMapper;
    
    @Autowired
    private ProjectInfoMapper projectInfoMapper;
    
    @Autowired
    private CaptchaService captchaService;

    /**
     * 查询全量国家信息
     * @return 国家信息Map，key为par_key，value为par_value
     */
    @PostMapping("/query/country")
    public R queryCountry() {
        try {
            List<InfoParameters> countryList = infoParametersMapper.selectByType("country");
            Map<String, String> countryMap = new HashMap<>();
            for (InfoParameters param : countryList) {
                countryMap.put(param.getParKey(), param.getParValue());
            }
            return R.success(countryMap);
        } catch (Exception e) {
            return R.error("查询国家信息失败：" + e.getMessage());
        }
    }

    /**
     * 查询全量民族信息
     * @return 民族信息Map，key为par_key，value为par_value
     */
    @PostMapping("/query/nation")
    public R queryNation() {
        try {
            List<InfoParameters> nationList = infoParametersMapper.selectByType("nation");
            Map<String, String> nationMap = new HashMap<>();
            for (InfoParameters param : nationList) {
                nationMap.put(param.getParKey(), param.getParValue());
            }
            return R.success(nationMap);
        } catch (Exception e) {
            return R.error("查询民族信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询企业ID和企业名称
     * @return 企业信息Map，key为id，value为enterprise_name
     */
    @PostMapping("/query/enterprise")
    public R queryEnterprise() {
        try {
            List<BusinessEnterprise> enterpriseList = businessEnterpriseMapper.selectList(null);
            Map<String, String> enterpriseMap = new HashMap<>();
            for (BusinessEnterprise enterprise : enterpriseList) {
                enterpriseMap.put(enterprise.getId(), enterprise.getEnterpriseName());
            }
            return R.success(enterpriseMap);
        } catch (Exception e) {
            return R.error("查询企业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取验证码
     * @return 包含验证码ID和图片数据的Map
     */
    @PostMapping("/captcha")
    public R getCaptcha() {
        try {
            // 生成5分钟过期的验证码
            Map<String, Object> captchaResult = captchaService.generateCaptcha(5);
            return R.success(captchaResult);
        } catch (Exception e) {
            return R.error("获取验证码失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证验证码
     * @param params 包含captchaId和userInput的参数
     * @return 验证结果
     */
    @PostMapping("/validate-captcha")
    public R validateCaptcha(@RequestBody Map<String, String> params) {
        try {
            String captchaId = params.get("captchaId");
            String userInput = params.get("userInput");
            
            if (captchaId == null || userInput == null) {
                return R.error("验证码ID和用户输入不能为空");
            }
            
            boolean isValid = captchaService.validateCaptcha(captchaId, userInput);
            
            if (isValid) {
                return R.success("验证码验证成功");
            } else {
                return R.error("验证码错误或已失效");
            }
        } catch (Exception e) {
            return R.error("验证码验证失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询项目信息
     * @param applyStartTime 申请开始时间
     * @param applyEndTime 申请截止时间
     * @return 项目信息列表，包含id和project_name
     */
    @PostMapping("/query/project")
    public R queryProject(@RequestParam(required = false) String applyStartTime,
                         @RequestParam(required = false) String applyEndTime) {
        try {
            QueryWrapper<ProjectInfo> queryWrapper = new QueryWrapper<>();
            
            // 根据时间范围筛选
            if (applyStartTime != null && !applyStartTime.isEmpty()) {
                queryWrapper.ge("apply_start_time", applyStartTime);
            }
            if (applyEndTime != null && !applyEndTime.isEmpty()) {
                queryWrapper.le("apply_end_time", applyEndTime);
            }
            
            // 只查询开启状态的项目
            queryWrapper.eq("status", 1);
            
            // 按申请开始时间升序排列
            queryWrapper.orderByAsc("apply_start_time");
            
            List<ProjectInfo> projectList = projectInfoMapper.selectList(queryWrapper);
            
            // 转换为DTO格式
            List<ProjectInfoDTO> result = new ArrayList<>();
            for (ProjectInfo project : projectList) {
                ProjectInfoDTO dto = new ProjectInfoDTO();
                dto.setId(project.getId());
                dto.setProjectName(project.getProjectName());
                result.add(dto);
            }
            
            return R.success(result);
            
        } catch (Exception e) {
            return R.error("查询项目信息失败：" + e.getMessage());
        }
    }
}