package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.*;
import com.example.spba.domain.dto.ChooseHouseTask;
import com.example.spba.domain.entity.*;
import com.example.spba.service.ChooseHouseService;
import com.example.spba.utils.UserContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 选房任务服务实现类
 */
@Service
public class ChooseHouseServiceImpl implements ChooseHouseService {

    private static final Logger logger = LoggerFactory.getLogger(ChooseHouseServiceImpl.class);

    @Autowired
    private ApplicationAgencyTalentMapper applicationAgencyTalentMapper;

    @Autowired
    private ApplicationIndustryTalentMapper applicationIndustryTalentMapper;

    @Autowired
    private ApplicationLeadingTalentMapper applicationLeadingTalentMapper;

    @Autowired
    private EnterpriseChoicehouseTimeMapper enterpriseChoicehouseTimeMapper;

    @Autowired
    private TalentChoicehouseTimeMapper talentChoicehouseTimeMapper;

    @Autowired
    private ProjectCommunityMapper projectCommunityMapper;

    @Autowired
    private CommunityInfoMapper communityInfoMapper;

    @Autowired
    private BusinessUserMapper businessUserMapper;

    // 用户类型常量定义
    /** 企业员工 */
    private static final int USER_TYPE_ENTERPRISE_EMPLOYEE = 1;
    /** 机关单位员工 */
    private static final int USER_TYPE_GOVERNMENT_EMPLOYEE = 2;
    /** 领军人才 */
    private static final int USER_TYPE_LEADING_TALENT = 3;
    /** 优青人才 */
    private static final int USER_TYPE_YOUNG_TALENT = 4;

    /**
     * 申请状态：待选房 (15-确定配租/待选房)
     */
    private static final int APPLY_STATUS_WAITING_CHOOSE_HOUSE = 15;

    @Override
    public ChooseHouseTask checkUserChooseHouseQualification(String userId) {
        ChooseHouseTask task = new ChooseHouseTask();
        
        try {
            // 获取用户类型
            businessUserMapper.selectById(userId);

            Integer userType = businessUserMapper.selectById(userId).getRegType();
            logger.info("[选房资格判断] userId: {}, userType: {}", userId, userType);

            if (userType == null) {
                logger.warn("[选房资格判断] 用户类型为空，userId: {}", userId);
                task.setIsApproved(false);
                task.setIsSelectionTime(false);
                return task;
            }

            // 根据用户类型查询申请信息
            ApplicationInfoBuilder infoBuilder = buildApplicationInfo(userId, userType);
            if (infoBuilder == null || !infoBuilder.hasData()) {
                logger.info("[选房资格判断] 未找到有效的申请信息，userId: {}, userType: {}", userId, userType);
                task.setIsApproved(false);
                task.setIsSelectionTime(false);
                return task;
            }

            // 构建申请信息
            ChooseHouseTask.ApplicationInfo appInfo = infoBuilder.build();
            task.setApplicationInfo(appInfo);

            // 判断是否有待选房的申请（状态为 15）
            boolean hasWaitingApplication = infoBuilder.hasWaitingApplication();
            task.setIsApproved(hasWaitingApplication);
            logger.info("[选房资格判断] 资格审核结果：{}", hasWaitingApplication ? "通过" : "不通过");

            // 判断是否在选房时间内
            boolean isInSelectionTime = checkIsInSelectionTime(userId, userType, appInfo.getProjectId());
            task.setIsSelectionTime(isInSelectionTime);
            logger.info("[选房资格判断] 选房时间判断结果：{}", isInSelectionTime ? "是" : "否");

            // 设置选房开始和结束时间
            String[] selectionTimes = getSelectionTime(appInfo.getProjectId(), userType);
            if (selectionTimes != null && selectionTimes.length >= 2) {
                task.setSelectionStartTime(selectionTimes[0]);
                task.setSelectionEndTime(selectionTimes[1]);
            }

        } catch (Exception e) {
            logger.error("[选房资格判断] 判断失败，userId: {}, error: {}", userId, e.getMessage(), e);
            task.setIsApproved(false);
            task.setIsSelectionTime(false);
        }

        return task;
    }

    /**
     * 根据用户类型构建申请信息
     */
    private ApplicationInfoBuilder buildApplicationInfo(String userId, Integer userType) {
        switch (userType) {
            case USER_TYPE_ENTERPRISE_EMPLOYEE:
                // 企业员工 - 从 industry_talent 表查询
                return queryFromIndustryTalent(userId);
            
            case USER_TYPE_GOVERNMENT_EMPLOYEE:
                // 机关单位员工 - 从 agency_talent 表查询
                return queryFromAgencyTalent(userId);
            
            case USER_TYPE_LEADING_TALENT:
                // 领军人才 - 从 leading_talent 表查询
                return queryFromLeadingTalent(userId);
            
            case USER_TYPE_YOUNG_TALENT:
                // 优青人才 - 从 industry_talent 表查询（假设与行业人才相同）
                return queryFromIndustryTalent(userId);
            
            default:
                logger.warn("[申请信息构建] 未知的用户类型：{}", userType);
                return null;
        }
    }

    /**
     * 从行业人才申请表查询
     */
    private ApplicationInfoBuilder queryFromIndustryTalent(String userId) {
        LambdaQueryWrapper<ApplicationIndustryTalent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationIndustryTalent::getApplicantId, userId);
        queryWrapper.orderByDesc(ApplicationIndustryTalent::getApplyDate, ApplicationIndustryTalent::getApplyTime);
        
        List<ApplicationIndustryTalent> applications = applicationIndustryTalentMapper.selectList(queryWrapper);
        return new ApplicationInfoBuilder(applications, APPLY_STATUS_WAITING_CHOOSE_HOUSE);
    }

    /**
     * 从机构人才申请表查询
     */
    private ApplicationInfoBuilder queryFromAgencyTalent(String userId) {
        LambdaQueryWrapper<ApplicationAgencyTalent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationAgencyTalent::getApplicantId, userId);
        queryWrapper.orderByDesc(ApplicationAgencyTalent::getApplyDate, ApplicationAgencyTalent::getApplyTime);
        
        List<ApplicationAgencyTalent> applications = applicationAgencyTalentMapper.selectList(queryWrapper);
        return new ApplicationInfoBuilder(applications, APPLY_STATUS_WAITING_CHOOSE_HOUSE);
    }

    /**
     * 从领军人才申请表查询
     */
    private ApplicationInfoBuilder queryFromLeadingTalent(String userId) {
        LambdaQueryWrapper<ApplicationLeadingTalent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationLeadingTalent::getApplicantId, userId);
        queryWrapper.orderByDesc(ApplicationLeadingTalent::getApplyDate, ApplicationLeadingTalent::getApplyTime);
        
        List<ApplicationLeadingTalent> applications = applicationLeadingTalentMapper.selectList(queryWrapper);
        return new ApplicationInfoBuilder(applications, APPLY_STATUS_WAITING_CHOOSE_HOUSE);
    }

    /**
     * 检查是否在选房时间内
     */
    private boolean checkIsInSelectionTime(String userId, Integer userType, String projectId) {
        try {
            // 根据用户类型查询不同的选房时间表
            if (userType == USER_TYPE_GOVERNMENT_EMPLOYEE || userType == USER_TYPE_LEADING_TALENT || userType == USER_TYPE_YOUNG_TALENT) {
                // 人才用户查询人才选房时间表
                LambdaQueryWrapper<TalentChoicehouseTime> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TalentChoicehouseTime::getProjectId, projectId);
                TalentChoicehouseTime choicehouseTime = talentChoicehouseTimeMapper.selectOne(queryWrapper);
                
                if (choicehouseTime != null) {
                    return isCurrentTimeInRange(choicehouseTime.getStartTime(),
                                               choicehouseTime.getEndTime());
                }
            }
        } catch (Exception e) {
            logger.error("[选房时间判断] 判断失败，userId: {}, projectId: {}, error: {}", userId, projectId, e.getMessage());
        }
        
        return false;
    }

    /**
     * 获取选房时间
     */
    private String[] getSelectionTime(String projectId, Integer userType) {
        try {
            if (userType == USER_TYPE_GOVERNMENT_EMPLOYEE || userType == USER_TYPE_LEADING_TALENT || userType == USER_TYPE_YOUNG_TALENT) {
                LambdaQueryWrapper<TalentChoicehouseTime> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TalentChoicehouseTime::getProjectId, projectId);
                TalentChoicehouseTime choicehouseTime = talentChoicehouseTimeMapper.selectOne(queryWrapper);
                
                if (choicehouseTime != null) {
                    return new String[]{
                        choicehouseTime.getStartTime(),
                        choicehouseTime.getEndTime()
                    };
                }
            }
        } catch (Exception e) {
            logger.error("[获取选房时间] 获取失败，projectId: {}, userType: {}, error: {}", projectId, userType, e.getMessage());
        }
        
        return null;
    }

    @Override
    public List<CommunityInfo> queryAvailableCommunitiesForUser(String userId) {
        try {
            logger.info("[查询可用小区] 开始查询，userId: {}", userId);
            
            // 1. 获取用户类型
            Integer userType = UserContextUtil.getUserType();
            logger.info("[查询可用小区] userId: {}, userType: {}", userId, userType);
            
            if (userType == null) {
                logger.warn("[查询可用小区] 用户类型为空，userId: {}", userId);
                return new ArrayList<>();
            }
            
            // 2. 根据用户类型查询申请信息，获取项目 ID
            ApplicationInfoBuilder infoBuilder = buildApplicationInfo(userId, userType);
            if (infoBuilder == null || !infoBuilder.hasData()) {
                logger.info("[查询可用小区] 未找到有效的申请信息，userId: {}, userType: {}", userId, userType);
                return new ArrayList<>();
            }
            
            ChooseHouseTask.ApplicationInfo appInfo = infoBuilder.build();
            String projectId = appInfo.getProjectId();
            logger.info("[查询可用小区] 获取到项目 ID: {}", projectId);
            
            if (projectId == null || projectId.isEmpty()) {
                logger.warn("[查询可用小区] 项目 ID 为空，userId: {}", userId);
                return new ArrayList<>();
            }
            
            // 3. 判断是否在选房时间内
            boolean isInSelectionTime = checkIsInSelectionTime(userId, userType, projectId);
            if (!isInSelectionTime) {
                logger.info("[查询可用小区] 不在选房时间内，userId: {}, projectId: {}", userId, projectId);
                return new ArrayList<>(); // 不在选房时间，返回空列表
            }
            logger.info("[查询可用小区] 用户在选房时间内，projectId: {}", projectId);
            
            // 4. 从 project_community 获取小区 ID 列表
            List<String> communityIds = projectCommunityMapper.selectCommunityIdsByProjectId(projectId);
            if (communityIds == null || communityIds.isEmpty()) {
                logger.info("[查询可用小区] 项目下没有关联的小区，projectId: {}", projectId);
                return new ArrayList<>();
            }
            logger.info("[查询可用小区] 找到 {} 个小区，projectId: {}", communityIds.size(), projectId);
            
            // 5. 从 community_info 查询小区详细信息（包括 tag1, tag2, tag3）
            List<CommunityInfo> communities = communityInfoMapper.selectByIds(communityIds);
            if (communities == null || communities.isEmpty()) {
                logger.info("[查询可用小区] 未找到小区详细信息，communityIds: {}", communityIds);
                return new ArrayList<>();
            }
            
            logger.info("[查询可用小区] 查询成功，userId: {}, 返回 {} 个小区信息", userId, communities.size());
            return communities;
            
        } catch (Exception e) {
            logger.error("[查询可用小区] 查询失败，userId: {}, error: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 判断当前时间是否在指定时间范围内
     */
    private boolean isCurrentTimeInRange(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }

        try {
            String currentTime = getCurrentDateTime();
            // 时间格式：yyyy-MM-dd HH:mm:ss
            return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) <= 0;
        } catch (Exception e) {
            logger.error("[时间范围判断] 判断失败，startTime: {}, endTime: {}, error: {}", startTime, endTime, e.getMessage());
            return false;
        }
    }

    /**
     * 获取当前日期时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String getCurrentDateTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new java.util.Date());
    }

    /**
     * 应用信息构建器
     */
    private static class ApplicationInfoBuilder {
        private List<?> applications;
        private int waitingStatus;

        public ApplicationInfoBuilder(List<?> applications, int waitingStatus) {
            this.applications = applications;
            this.waitingStatus = waitingStatus;
        }

        public boolean hasData() {
            return applications != null && !applications.isEmpty();
        }

        public boolean hasWaitingApplication() {
            if (!hasData()) {
                return false;
            }
            
            // 检查是否有状态为待选房的申请
            for (Object app : applications) {
                Integer status = getApplyStatus(app);
                if (status != null && status == waitingStatus) {
                    return true;
                }
            }
            return false;
        }

        public ChooseHouseTask.ApplicationInfo build() {
            if (!hasData()) {
                return null;
            }

            // 取最新的申请记录
            Object latestApp = applications.get(0);
            
            ChooseHouseTask.ApplicationInfo info = new ChooseHouseTask.ApplicationInfo();
            info.setProjectId(getProjectId(latestApp));
            info.setApplyDateTime(getApplyDateTime(latestApp));
            info.setExpectedCommunity(getExpectedCommunity(latestApp));
            info.setExpectedHouseType(getExpectedHouseType(latestApp));
            
            return info;
        }

        // 通用的字段提取方法
        private String getProjectId(Object app) {
            if (app instanceof ApplicationIndustryTalent) {
                return ((ApplicationIndustryTalent) app).getProjectId();
            } else if (app instanceof ApplicationAgencyTalent) {
                return ((ApplicationAgencyTalent) app).getProjectId();
            } else if (app instanceof ApplicationLeadingTalent) {
                return ((ApplicationLeadingTalent) app).getProjectId();
            }
            return null;
        }

        private String getApplyDateTime(Object app) {
            String date = null;
            String time = null;
            
            if (app instanceof ApplicationIndustryTalent) {
                date = ((ApplicationIndustryTalent) app).getApplyDate();
                time = ((ApplicationIndustryTalent) app).getApplyTime();
            } else if (app instanceof ApplicationAgencyTalent) {
                date = ((ApplicationAgencyTalent) app).getApplyDate();
                time = ((ApplicationAgencyTalent) app).getApplyTime();
            } else if (app instanceof ApplicationLeadingTalent) {
                date = ((ApplicationLeadingTalent) app).getApplyDate();
                time = ((ApplicationLeadingTalent) app).getApplyTime();
            }
            
            if (date != null && time != null) {
                return date + " " + time;
            }
            return date != null ? date : time;
        }

        private Integer getApplyStatus(Object app) {
            if (app instanceof ApplicationIndustryTalent) {
                return ((ApplicationIndustryTalent) app).getApplyStatus();
            } else if (app instanceof ApplicationAgencyTalent) {
                return ((ApplicationAgencyTalent) app).getApplyStatus();
            } else if (app instanceof ApplicationLeadingTalent) {
                return ((ApplicationLeadingTalent) app).getApplyStatus();
            }
            return null;
        }

        private String[] getExpectedCommunity(Object app) {
            List<String> communities = new ArrayList<>();
            
            if (app instanceof ApplicationIndustryTalent) {
                ApplicationIndustryTalent talent = (ApplicationIndustryTalent) app;
                if (talent.getCommunityId1() != null) communities.add(talent.getCommunityId1());
                if (talent.getCommunityId2() != null) communities.add(talent.getCommunityId2());
                if (talent.getCommunityId3() != null) communities.add(talent.getCommunityId3());
            } else if (app instanceof ApplicationAgencyTalent) {
                ApplicationAgencyTalent talent = (ApplicationAgencyTalent) app;
                if (talent.getCommunityId1() != null) communities.add(talent.getCommunityId1());
                if (talent.getCommunityId2() != null) communities.add(talent.getCommunityId2());
                if (talent.getCommunityId3() != null) communities.add(talent.getCommunityId3());
            } else if (app instanceof ApplicationLeadingTalent) {
                ApplicationLeadingTalent talent = (ApplicationLeadingTalent) app;
                if (talent.getCommunityId1() != null) communities.add(talent.getCommunityId1());
                if (talent.getCommunityId2() != null) communities.add(talent.getCommunityId2());
                if (talent.getCommunityId3() != null) communities.add(talent.getCommunityId3());
            }
            
            return communities.toArray(new String[0]);
        }

        private String[] getExpectedHouseType(Object app) {
            List<String> houseTypes = new ArrayList<>();
            
            if (app instanceof ApplicationIndustryTalent) {
                ApplicationIndustryTalent talent = (ApplicationIndustryTalent) app;
                if (talent.getHouseType1() != null) houseTypes.add(String.valueOf(talent.getHouseType1()));
                if (talent.getHouseType2() != null) houseTypes.add(String.valueOf(talent.getHouseType2()));
                if (talent.getHouseType3() != null) houseTypes.add(String.valueOf(talent.getHouseType3()));
                if (talent.getHouseType4() != null) houseTypes.add(String.valueOf(talent.getHouseType4()));
            } else if (app instanceof ApplicationAgencyTalent) {
                ApplicationAgencyTalent talent = (ApplicationAgencyTalent) app;
                if (talent.getHouseType1() != null) houseTypes.add(String.valueOf(talent.getHouseType1()));
                if (talent.getHouseType2() != null) houseTypes.add(String.valueOf(talent.getHouseType2()));
                if (talent.getHouseType3() != null) houseTypes.add(String.valueOf(talent.getHouseType3()));
                if (talent.getHouseType4() != null) houseTypes.add(String.valueOf(talent.getHouseType4()));
            } else if (app instanceof ApplicationLeadingTalent) {
                ApplicationLeadingTalent talent = (ApplicationLeadingTalent) app;
                if (talent.getHouseType1() != null) houseTypes.add(String.valueOf(talent.getHouseType1()));
                if (talent.getHouseType2() != null) houseTypes.add(String.valueOf(talent.getHouseType2()));
                if (talent.getHouseType3() != null) houseTypes.add(String.valueOf(talent.getHouseType3()));
                if (talent.getHouseType4() != null) houseTypes.add(String.valueOf(talent.getHouseType4()));
            }
            
            return houseTypes.toArray(new String[0]);
        }
    }
}
