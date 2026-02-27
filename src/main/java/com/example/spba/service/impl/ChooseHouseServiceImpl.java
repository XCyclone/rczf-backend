package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.*;
import com.example.spba.domain.entity.*;
import com.example.spba.service.ChooseHouseService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 选房相关服务实现类
 * @author Generated
 */
@Service
public class ChooseHouseServiceImpl implements ChooseHouseService {
    
    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Autowired
    private BusinessEnterpriseMapper businessEnterpriseMapper;

    @Autowired
    private ApplicationAgencyTalentMapper applicationAgencyTalentMapper;
    
    @Autowired
    private ApplicationIndustryMapper applicationIndustryMapper;
    
    @Autowired
    private ApplicationLeadingTalentMapper applicationLeadingTalentMapper;
    
    @Autowired
    private TalentChoicehouseTimeMapper talentChoicehouseTimeMapper;
    
    @Autowired
    private EnterpriseChoicehouseTimeMapper enterpriseChoicehouseTimeMapper;

    @Override
    public R enterpriseJudgeChooseHouseTime(String userId) {
        try {
            // 1. 查询企业用户信息
            BusinessEnterprise enterpriseUser = businessEnterpriseMapper.selectById(userId);
            if (enterpriseUser == null) {
                return R.error("企业用户信息不存在");
            }

            // 2. 查询该企业申请记录
            Object latestApplication = getLatestIndustryApplicationApprovedByHousing(userId);
            if (latestApplication == null) {
                return R.success(false, "未找到符合条件的企业申请记录");
            }
            
            // 3. 获取申请ID
            String applicationId = ((ApplicationIndustry) latestApplication).getApplicationId();
            if (applicationId == null || applicationId.isEmpty()) {
                return R.success(false, "申请记录ID无效");
            }
            
            // 6. 查询企业选房时间表判断是否在选房时间内
            boolean isInTimeRange = checkEnterpriseChooseHouseTime(applicationId);
            
            return R.success(isInTimeRange);
            
        } catch (Exception e) {
            return R.error("判断企业选房时间失败：" + e.getMessage());
        }
    }
    
    @Override
    public R judgeChooseHouseTime(String userId) {
        try {
            // 1. 查询用户信息
            BusinessUser user = businessUserMapper.selectById(userId);
            if (user == null) {
                return R.error("用户信息不存在");
            }
            
            // 2. 根据用户类型查询对应的申请记录
            Object latestApplication = getLatestApprovedApplication(userId, user.getRegType());
            if (latestApplication == null) {
                return R.success(false, "未找到符合条件的申请记录");
            }
            
            // 3. 获取申请ID
            String applicationId = getApplicationId(latestApplication);
            if (applicationId == null || applicationId.isEmpty()) {
                return R.success(false, "申请记录ID无效");
            }
            
            // 4. 根据用户类型查询对应的选房时间表
            boolean isInTimeRange = checkChooseHouseTime(user.getRegType(), applicationId);
            
            return R.success(isInTimeRange);
            
        } catch (Exception e) {
            return R.error("判断选房时间失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取企业下最新的产业人才住建委审核通过的申请记录
     * @param companyId 企业ID
     * @return 最新的申请记录
     */
    private Object getLatestIndustryApplicationApprovedByHousing(String companyId) {
        QueryWrapper<ApplicationIndustry> wrapper = new QueryWrapper<>();
        wrapper.eq("applicant_company_id", companyId);
        wrapper.eq("apply_status", 4); // 审核通过状态
        wrapper.orderByDesc("apply_date", "apply_time");
        wrapper.last("LIMIT 1");
        
        return applicationIndustryMapper.selectOne(wrapper);
    }
    
    /**
     * 检查企业选房时间是否在有效期内
     * @param applicationId 申请ID
     * @return 是否在选房时间内
     */
    private boolean checkEnterpriseChooseHouseTime(String applicationId) {
        try {
            String currentTime = Time.getNowTimeDate("yyyy-MM-dd HH:mm:ss");
            
            // 查询企业选房时间表
            QueryWrapper<EnterpriseChoicehouseTime> wrapper = new QueryWrapper<>();
            wrapper.eq("application_id", applicationId);
            
            List<EnterpriseChoicehouseTime> timeRecords = enterpriseChoicehouseTimeMapper.selectList(wrapper);
            return isCurrentTimeInRange(timeRecords, currentTime);
            
        } catch (Exception e) {
            // 发生异常时返回false
            return false;
        }
    }
    
    /**
     * 根据用户类型获取最新的已审批通过的申请记录
     * @param userId 用户ID
     * @param regType 用户类型
     * @return 最新的申请记录
     */
    private Object getLatestApprovedApplication(String userId, Integer regType) {
        QueryWrapper<?> wrapper = new QueryWrapper<>();
        wrapper.eq("applicant_id", userId);
        wrapper.eq("apply_status", 3); // 工作单位审核通过状态
        wrapper.orderByDesc("apply_date", "apply_time");
        wrapper.last("LIMIT 1");
        
        switch (regType) {
            case 2: // 机关单位
                return applicationAgencyTalentMapper.selectOne((QueryWrapper<ApplicationAgencyTalent>) wrapper);
            case 3: // 领军人才
                return applicationLeadingTalentMapper.selectOne((QueryWrapper<ApplicationLeadingTalent>) wrapper);
            case 4: // 优青人才
                return applicationLeadingTalentMapper.selectOne((QueryWrapper<ApplicationLeadingTalent>) wrapper);
            default:
                return null;
        }
    }
    
    /**
     * 获取申请记录的applicationId
     * @param application 申请记录对象
     * @return applicationId
     */
    private String getApplicationId(Object application) {
        if (application == null) {
            return null;
        }
        
        try {
            if (application instanceof ApplicationAgencyTalent) {
                return ((ApplicationAgencyTalent) application).getApplicationId();
            } else if (application instanceof ApplicationIndustryTalent) {
                return ((ApplicationIndustryTalent) application).getApplicationId();
            } else if (application instanceof ApplicationLeadingTalent) {
                return ((ApplicationLeadingTalent) application).getApplicationId();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        return null;
    }
    
    /**
     * 检查选房时间是否在有效期内
     * @param regType 用户类型
     * @param applicationId 申请ID
     * @return 是否在选房时间内
     */
    private boolean checkChooseHouseTime(Integer regType, String applicationId) {
        try {
            String currentTime = Time.getNowTimeDate("yyyy-MM-dd HH:mm:ss");

            // 个人用户查询个人选房时间表
            QueryWrapper<TalentChoicehouseTime> wrapper = new QueryWrapper<>();
            wrapper.eq("application_id", applicationId);

            List<TalentChoicehouseTime> timeRecords = talentChoicehouseTimeMapper.selectList(wrapper);
            return isCurrentTimeInRange(timeRecords, currentTime);

        } catch (Exception e) {
            // 发生异常时返回false
            return false;
        }
    }
    
    /**
     * 判断当前时间是否在给定的时间范围内
     * @param timeRecords 时间记录列表
     * @param currentTime 当前时间
     * @return 是否在时间范围内
     */
    private boolean isCurrentTimeInRange(List<?> timeRecords, String currentTime) {
        if (timeRecords == null || timeRecords.isEmpty()) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDateTime = sdf.parse(currentTime);
            
            for (Object record : timeRecords) {
                String startTime = getStartTime(record);
                String endTime = getEndTime(record);
                
                if (startTime != null && endTime != null) {
                    Date startDate = sdf.parse(startTime);
                    Date endDate = sdf.parse(endTime);
                    
                    // 判断当前时间是否在开始时间和结束时间之间
                    if (currentDateTime.after(startDate) && currentDateTime.before(endDate)) {
                        return true;
                    }
                }
            }
        } catch (ParseException e) {
            // 时间解析异常时返回false
            return false;
        }
        
        return false;
    }
    
    /**
     * 获取开始时间
     * @param record 时间记录对象
     * @return 开始时间字符串
     */
    private String getStartTime(Object record) {
        try {
            if (record instanceof EnterpriseChoicehouseTime) {
                return ((EnterpriseChoicehouseTime) record).getStartTime();
            } else if (record instanceof TalentChoicehouseTime) {
                return ((TalentChoicehouseTime) record).getStartTime();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return null;
    }
    
    /**
     * 获取结束时间
     * @param record 时间记录对象
     * @return 结束时间字符串
     */
    private String getEndTime(Object record) {
        try {
            if (record instanceof EnterpriseChoicehouseTime) {
                return ((EnterpriseChoicehouseTime) record).getEndTime();
            } else if (record instanceof TalentChoicehouseTime) {
                return ((TalentChoicehouseTime) record).getEndTime();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return null;
    }
}