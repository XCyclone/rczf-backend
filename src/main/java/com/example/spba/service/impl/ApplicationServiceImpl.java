package com.example.spba.service.impl;

import com.example.spba.service.ApplicationAgencyService;
import com.example.spba.service.ApplicationIndustryService;
import com.example.spba.service.ApplicationLeadingService;
import com.example.spba.service.ApplicationService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 统一申请服务实现类
 * 处理跨类型的申请记录查询功能
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {
    
    @Autowired
    private ApplicationAgencyService applicationAgencyService;
    
    @Autowired
    private ApplicationIndustryService applicationIndustryService;
    
    @Autowired
    private ApplicationLeadingService applicationLeadingService;

    @Override
    public R queryAllUserApplications(String userId) {
        try {
            // 1. 并行查询三种类型的申请记录
            R agencyResult = applicationAgencyService.queryAllApplications(userId);
            R industryResult = applicationIndustryService.queryAllApplications(userId);
            R leadingResult = applicationLeadingService.queryAllApplications(userId);
            
            // 2. 合并所有申请记录
            List<Object> allApplications = new ArrayList<>();
            
            // 添加机关单位申请记录
            if (agencyResult.getCode() == 0 && agencyResult.getData() != null) {
                List<?> agencyApplications = (List<?>) agencyResult.getData();
                allApplications.addAll(agencyApplications);
            }
            
            // 添加产业人才申请记录
            if (industryResult.getCode() == 0 && industryResult.getData() != null) {
                List<?> industryApplications = (List<?>) industryResult.getData();
                allApplications.addAll(industryApplications);
            }
            
            // 添加领军优青人才申请记录
            if (leadingResult.getCode() == 0 && leadingResult.getData() != null) {
                List<?> leadingApplications = (List<?>) leadingResult.getData();
                allApplications.addAll(leadingApplications);
            }
            
            // 3. 按申请时间排序（倒序）
            Collections.sort(allApplications, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    try {
                        String date1 = getApplyDate(o1);
                        String date2 = getApplyDate(o2);
                        String time1 = getApplyTime(o1);
                        String time2 = getApplyTime(o2);
                        
                        // 先按日期比较
                        int dateCompare = date2.compareTo(date1);
                        if (dateCompare != 0) {
                            return dateCompare;
                        }
                        // 日期相同时按时间比较
                        return time2.compareTo(time1);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
            
            return R.success(allApplications);
            
        } catch (Exception e) {
            return R.error("查询用户申请记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取申请日期
     */
    private String getApplyDate(Object application) {
        try {
            if (application == null) return "";
            
            // 使用反射获取属性值
            java.lang.reflect.Method method = application.getClass().getMethod("getApplyDate");
            Object result = method.invoke(application);
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * 获取申请时间
     */
    private String getApplyTime(Object application) {
        try {
            if (application == null) return "";
            
            // 使用反射获取属性值
            java.lang.reflect.Method method = application.getClass().getMethod("getApplyTime");
            Object result = method.invoke(application);
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}