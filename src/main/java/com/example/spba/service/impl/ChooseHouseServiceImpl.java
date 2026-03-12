package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.dao.*;
import com.example.spba.domain.dto.ChooseHouseSubmitDTO;
import com.example.spba.domain.dto.ChooseHouseTask;
import com.example.spba.domain.dto.HouseInfoDTO;
import com.example.spba.domain.dto.HouseInfoQueryDTO;
import com.example.spba.domain.entity.*;
import com.example.spba.service.ChooseHouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 选房任务服务实现类
 */
@Service
public class ChooseHouseServiceImpl implements ChooseHouseService {

    private static final Logger logger = LoggerFactory.getLogger(ChooseHouseServiceImpl.class);
    /**
     * 企业员工
     */
    private static final int USER_TYPE_ENTERPRISE_EMPLOYEE = 1;
    /**
     * 机关单位员工
     */
    private static final int USER_TYPE_GOVERNMENT_EMPLOYEE = 2;
    /**
     * 领军人才
     */
    private static final int USER_TYPE_LEADING_TALENT = 3;
    /**
     * 优青人才
     */
    private static final int USER_TYPE_YOUNG_TALENT = 4;
    /**
     * 申请状态：待选房 (15-确定配租/待选房)
     */
    private static final int APPLY_STATUS_WAITING_CHOOSE_HOUSE = 15;
    @Autowired
    private ApplicationAgencyTalentMapper applicationAgencyTalentMapper;
    @Autowired
    private ApplicationIndustryTalentMapper applicationIndustryTalentMapper;
    @Autowired
    private ApplicationLeadingTalentMapper applicationLeadingTalentMapper;

    @Autowired
    private TalentChoicehouseTimeMapper talentChoicehouseTimeMapper;
    @Autowired
    private ProjectCommunityMapper projectCommunityMapper;
    @Autowired
    private CommunityInfoMapper communityInfoMapper;
    @Autowired
    private BusinessUserMapper businessUserMapper;
    @Autowired
    private PublicParametersMapper publicParametersMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;

    // 用户类型常量定义
    @Autowired
    private HouseInfoMapper houseInfoMapper;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private HouseChoiceMapper houseChoiceMapper;

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
            boolean isInSelectionTime = checkIsInSelectionTime(userId, userType, appInfo.getProjectId(), appInfo.getApplicationId());
            task.setIsSelectionTime(isInSelectionTime);
            logger.info("[选房资格判断] 选房时间判断结果：{}", isInSelectionTime ? "是" : "否");

            // 设置选房开始和结束时间
            String[] selectionTimes = getSelectionTime(appInfo.getProjectId(), userType, appInfo.getApplicationId());
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
                return queryFromLeadingTalent(userId);

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
     * 检查是否在选房时间内（包括正常选房时间和补选时间）
     */
    private boolean checkIsInSelectionTime(String userId, Integer userType, String projectId, String applicationId) {
        try {
            // 根据用户类型查询不同的选房时间表
            if (userType == USER_TYPE_GOVERNMENT_EMPLOYEE || userType == USER_TYPE_LEADING_TALENT || userType == USER_TYPE_YOUNG_TALENT) {
                // 人才用户查询人才选房时间表
                LambdaQueryWrapper<TalentChoicehouseTime> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TalentChoicehouseTime::getProjectId, projectId);
                queryWrapper.eq(TalentChoicehouseTime::getApplicationId, applicationId);
                TalentChoicehouseTime choicehouseTime = talentChoicehouseTimeMapper.selectOne(queryWrapper);

                if (choicehouseTime != null) {
                    // 1. 首先检查是否在正常选房时间内
                    boolean isInNormalTime = isCurrentTimeInRange(choicehouseTime.getStartTime(),
                            choicehouseTime.getEndTime());
                    if (isInNormalTime) {
                        logger.info("[选房时间判断] 在正常选房时间内，userId: {}, projectId: {}", userId, projectId);
                        return true;
                    }
                }
                
                // 2. 如果不在正常选房时间，检查是否在补选时间内
                ProjectInfo projectInfo = projectInfoMapper.selectById(projectId);
                if (projectInfo != null && projectInfo.getBselectStartTime() != null && projectInfo.getBselectEndTime() != null) {
                    boolean isInSupplementTime = isCurrentTimeInRange(projectInfo.getBselectStartTime(),
                            projectInfo.getBselectEndTime());
                    if (isInSupplementTime) {
                        logger.info("[选房时间判断] 在补选时间内，userId: {}, projectId: {}", userId, projectId);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[选房时间判断] 判断失败，userId: {}, projectId: {}, error: {}", userId, projectId, e.getMessage());
        }

        logger.info("[选房时间判断] 不在选房时间内，userId: {}, projectId: {}", userId, projectId);
        return false;
    }

    /**
     * 获取选房时间（优先返回正常选房时间，如果没有则返回补选时间）
     */
    private String[] getSelectionTime(String projectId, Integer userType, String applicationId) {
        try {
            if (userType == USER_TYPE_GOVERNMENT_EMPLOYEE || userType == USER_TYPE_LEADING_TALENT || userType == USER_TYPE_YOUNG_TALENT) {
                // 1. 首先尝试获取正常选房时间
                LambdaQueryWrapper<TalentChoicehouseTime> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TalentChoicehouseTime::getProjectId, projectId);
                queryWrapper.eq(TalentChoicehouseTime::getApplicationId, applicationId);
                TalentChoicehouseTime choicehouseTime = talentChoicehouseTimeMapper.selectOne(queryWrapper);

                if (choicehouseTime != null && choicehouseTime.getStartTime() != null && choicehouseTime.getEndTime() != null) {
                    logger.info("[获取选房时间] 获取到正常选房时间，projectId: {}, startTime: {}, endTime: {}",
                            projectId, choicehouseTime.getStartTime(), choicehouseTime.getEndTime());
                    return new String[]{
                            choicehouseTime.getStartTime(),
                            choicehouseTime.getEndTime()
                    };
                }
                
                // 2. 如果没有正常选房时间，获取补选时间
                ProjectInfo projectInfo = projectInfoMapper.selectById(projectId);
                if (projectInfo != null && projectInfo.getBselectStartTime() != null && projectInfo.getBselectEndTime() != null) {
                    logger.info("[获取选房时间] 获取到补选时间，projectId: {}, startTime: {}, endTime: {}",
                            projectId, projectInfo.getBselectStartTime(), projectInfo.getBselectEndTime());
                    return new String[]{
                            projectInfo.getBselectStartTime(),
                            projectInfo.getBselectEndTime()
                    };
                }
            }
        } catch (Exception e) {
            logger.error("[获取选房时间] 获取失败，projectId: {}, userType: {}, error: {}", projectId, userType, e.getMessage());
        }

        logger.info("[获取选房时间] 未找到选房时间，projectId: {}", projectId);
        return null;
    }

//    @Override
//    public List<CommunityInfo> queryAvailableCommunitiesForUser(String userId) {
//        try {
//            logger.info("[查询可用小区] 开始查询，userId: {}", userId);
//
//            // 1. 获取用户类型
//            Integer userType = UserContextUtil.getUserType();
//            logger.info("[查询可用小区] userId: {}, userType: {}", userId, userType);
//
//            if (userType == null) {
//                logger.warn("[查询可用小区] 用户类型为空，userId: {}", userId);
//                return new ArrayList<>();
//            }
//
//            // 2. 根据用户类型查询申请信息，获取项目 ID
//            ApplicationInfoBuilder infoBuilder = buildApplicationInfo(userId, userType);
//            if (infoBuilder == null || !infoBuilder.hasData()) {
//                logger.info("[查询可用小区] 未找到有效的申请信息，userId: {}, userType: {}", userId, userType);
//                return new ArrayList<>();
//            }
//
//            ChooseHouseTask.ApplicationInfo appInfo = infoBuilder.build();
//            String projectId = appInfo.getProjectId();
//            logger.info("[查询可用小区] 获取到项目 ID: {}", projectId);
//
//            if (projectId == null || projectId.isEmpty()) {
//                logger.warn("[查询可用小区] 项目 ID 为空，userId: {}", userId);
//                return new ArrayList<>();
//            }
//
//            // 3. 判断是否在选房时间内
//            boolean isInSelectionTime = checkIsInSelectionTime(userId, userType, projectId,appInfo.getApplicationId());
//            if (!isInSelectionTime) {
//                logger.info("[查询可用小区] 不在选房时间内，userId: {}, projectId: {}", userId, projectId);
//                return new ArrayList<>(); // 不在选房时间，返回空列表
//            }
//            logger.info("[查询可用小区] 用户在选房时间内，projectId: {}", projectId);
//
//            // 4. 从 project_community 获取小区 ID 列表
//            List<String> communityIds = projectCommunityMapper.selectCommunityIdsByProjectId(projectId);
//            if (communityIds == null || communityIds.isEmpty()) {
//                logger.info("[查询可用小区] 项目下没有关联的小区，projectId: {}", projectId);
//                return new ArrayList<>();
//            }
//            logger.info("[查询可用小区] 找到 {} 个小区，projectId: {}", communityIds.size(), projectId);
//
//            // 5. 从 community_info 查询小区详细信息（包括 tag1, tag2, tag3）
//            List<CommunityInfo> communities = communityInfoMapper.selectByIds(communityIds);
//            if (communities == null || communities.isEmpty()) {
//                logger.info("[查询可用小区] 未找到小区详细信息，communityIds: {}", communityIds);
//                return new ArrayList<>();
//            }
//
//            logger.info("[查询可用小区] 查询成功，userId: {}, 返回 {} 个小区信息", userId, communities.size());
//            return communities;
//
//        } catch (Exception e) {
//            logger.error("[查询可用小区] 查询失败，userId: {}, error: {}", userId, e.getMessage(), e);
//            return new ArrayList<>();
//        }
//    }

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

    @Override
    public List<Map<String, String>> queryLocationTags() {
        try {
            logger.info("[查询位置标签] 开始查询位置标签列表");

            // 从 public_parameters 表中查询 par_type 为 "communityLocationTag" 的所有参数
            List<InfoParameters> parameters = publicParametersMapper.selectByParType("communityLocationTag");

            if (parameters == null || parameters.isEmpty()) {
                logger.info("[查询位置标签] 未找到位置标签数据");
                return new ArrayList<>();
            }

            // 转换为指定格式：{text: par_key, value: par_value}
            List<Map<String, String>> result = new ArrayList<>();
            for (InfoParameters param : parameters) {
                Map<String, String> tag = new HashMap<>();
                tag.put("text", param.getParKey());
                tag.put("value", param.getParValue());
                result.add(tag);
            }

            logger.info("[查询位置标签] 查询成功，共找到 {} 个位置标签", result.size());
            return result;

        } catch (Exception e) {
            logger.error("[查询位置标签] 查询失败，error: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Page<HouseInfoDTO> queryHouseInfoList(HouseInfoQueryDTO queryDTO) {
        try {
            logger.info("[查询房屋信息] 开始查询，projectId: {}, page: {}, size: {}",
                    queryDTO.getProjectId(), queryDTO.getPage(), queryDTO.getSize());

            // 调用 Mapper 查询全部数据
            List<HouseInfoDTO> allList = houseInfoMapper.selectHouseInfoListByPage(queryDTO);

            // 为每套房子填充图片 URL 列表
            for (HouseInfoDTO houseInfo : allList) {
                // 查询户型图图片 URL 列表
                if (houseInfo.getHouseLayoutPicId() != null && !houseInfo.getHouseLayoutPicId().isEmpty()) {
                    List<FileInfo> layoutPics = fileInfoMapper.selectByRelId(houseInfo.getHouseLayoutPicId());
                    List<String> layoutPicUrls = new ArrayList<>();
                    for (FileInfo pic : layoutPics) {
                        layoutPicUrls.add(pic.getFileUrl());
                    }
                    houseInfo.setHouseLayoutPicList(layoutPicUrls);
                } else {
                    houseInfo.setHouseLayoutPicList(new ArrayList<>());
                }

                // 查询实景图图片 URL 列表
                if (houseInfo.getHousePicGroupId() != null && !houseInfo.getHousePicGroupId().isEmpty()) {
                    List<FileInfo> housePics = fileInfoMapper.selectByRelId(houseInfo.getHousePicGroupId());
                    List<String> housePicUrls = new ArrayList<>();
                    for (FileInfo pic : housePics) {
                        housePicUrls.add(pic.getFileUrl());
                    }
                    houseInfo.setHousePicGroupList(housePicUrls);
                } else {
                    houseInfo.setHousePicGroupList(new ArrayList<>());
                }
            }

            // 计算总数
            long total = allList.size();

            // 创建分页对象
            Page<HouseInfoDTO> page = new Page<>(queryDTO.getPage(), queryDTO.getSize(), total);

            // 手动分页
            int fromIndex = (queryDTO.getPage() - 1) * queryDTO.getSize();
            int toIndex = Math.min(fromIndex + queryDTO.getSize(), allList.size());

            if (fromIndex < allList.size()) {
                page.setRecords(allList.subList(fromIndex, toIndex));
            } else {
                page.setRecords(new ArrayList<>());
            }

            logger.info("[查询房屋信息] 查询成功，共找到 {} 条记录", page.getRecords().size());
            return page;

        } catch (Exception e) {
            logger.error("[查询房屋信息] 查询失败，error: {}", e.getMessage(), e);
            return new Page<>(queryDTO.getPage(), queryDTO.getSize(), 0L);
        }
    }

    @Override
    public String submitChooseHouse(String userId, ChooseHouseSubmitDTO submitDTO) {
        try {
            logger.info("[提交选房] userId: {}, projectId: {}, houseId: {}, choiceSrc: {}",
                    userId, submitDTO.getProjectId(), submitDTO.getHouseId(), submitDTO.getChoiceSrc());

            // 1. 获取用户 ID
            if (userId == null || userId.isEmpty()) {
                throw new RuntimeException("用户未登录");
            }
            BusinessUser user = businessUserMapper.selectById(userId);

            // 2. 查询申请信息，判断用户类型
            String applicationId = null;
            Integer userType = null;
            String applicant_zjhm = null;
            ApplicationAgencyTalent agencyApp = null;
            ApplicationLeadingTalent leadingApp = null;

            // 先从 application_agency_talent 查询（政府机关）
            LambdaQueryWrapper<ApplicationAgencyTalent> agencyQuery = new LambdaQueryWrapper<>();
            agencyQuery.eq(ApplicationAgencyTalent::getApplicantId, userId)
                    .eq(ApplicationAgencyTalent::getProjectId, submitDTO.getProjectId())
                    .orderByDesc(ApplicationAgencyTalent::getApplyDate, ApplicationAgencyTalent::getApplyTime);
            agencyApp = applicationAgencyTalentMapper.selectOne(agencyQuery);

            if (agencyApp != null) {
                // 政府机关员工
                applicationId = agencyApp.getApplicationId();
                userType = USER_TYPE_GOVERNMENT_EMPLOYEE;
                applicant_zjhm = agencyApp.getApplicantZjhm();
                logger.info("[提交选房] 用户类型：政府机关员工，applicationId: {}", applicationId);
            } else {
                // 从 application_leading_talent 查询（领军优青）
                LambdaQueryWrapper<ApplicationLeadingTalent> leadingQuery = new LambdaQueryWrapper<>();
                leadingQuery.eq(ApplicationLeadingTalent::getApplicantId, userId)
                        .eq(ApplicationLeadingTalent::getProjectId, submitDTO.getProjectId())
                        .orderByDesc(ApplicationLeadingTalent::getApplyDate, ApplicationLeadingTalent::getApplyTime);
                leadingApp = applicationLeadingTalentMapper.selectOne(leadingQuery);

                if (leadingApp != null) {
                    // 领军优青人才
                    applicationId = leadingApp.getApplicationId();
                    // 领军人才和优青人才都使用同一个类型标识
                    userType = USER_TYPE_LEADING_TALENT;
                    logger.info("[提交选房] 用户类型：领军优青人才，applicationId: {}", applicationId);
                    applicant_zjhm = leadingApp.getApplicantZjhm();
                }
            }

            if (applicationId == null) {
                throw new RuntimeException("未找到有效的申请信息，请先提交申请");
            }

            // 3. 查询项目信息获取 projectCode
            ProjectInfo projectInfo = projectInfoMapper.selectById(submitDTO.getProjectId());
            if (projectInfo == null) {
                throw new RuntimeException("项目不存在");
            }
            String projectCode = projectInfo.getProjectCode();

            // 4. 检查房屋状态并更新
            QueryWrapper<HouseInfo> houseQuery = new QueryWrapper<>();
            houseQuery.eq("id", submitDTO.getHouseId());
            HouseInfo houseInfo = houseInfoMapper.selectById(submitDTO.getHouseId());
            if (houseInfo == null) {
                throw new RuntimeException("房屋不存在");
            }
            // 检查房屋状态是否为 0（未选）
            if (houseInfo.getStatus() != 0) {
                throw new RuntimeException("房屋已被选择，无法重复选房");
            }

            // 更新房屋状态为 1（已选）
            houseInfo.setStatus(1);
            int i = houseInfoMapper.updateById(houseInfo);
            if (i == 0) {
                throw new RuntimeException("更新房屋状态失败");
            }

            // 5. 创建选房记录
            HouseChoice houseChoice = new HouseChoice();
            houseChoice.setId(UUID.randomUUID().toString().replace("-", ""));
            houseChoice.setApplicationId(applicationId);
            houseChoice.setApplicationType(userType);
            houseChoice.setProjectCode(projectCode);
            houseChoice.setHouseId(submitDTO.getHouseId());
            houseChoice.setChoiceZjhm(applicant_zjhm);
            houseChoice.setChoiceSrc(submitDTO.getChoiceSrc());
            houseChoice.setChoiceDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            houseChoice.setChoiceTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            houseChoice.setOperatorName(user.getName());

            houseChoiceMapper.insert(houseChoice);

            // 6. 更新申请表状态为 16（选房完成）
            if (userType == USER_TYPE_GOVERNMENT_EMPLOYEE) {
                // 政府机关员工：更新 application_agency_talent
                if (agencyApp != null) {
                    agencyApp.setApplyStatus(16);
                    applicationAgencyTalentMapper.updateById(agencyApp);
                    logger.info("[提交选房] 已更新政府机关申请表状态为 16，applicationId: {}", applicationId);
                }
            } else if (userType == USER_TYPE_LEADING_TALENT) {
                // 领军优青人才：更新 application_leading_talent
                if (leadingApp != null) {
                    leadingApp.setApplyStatus(16);
                    applicationLeadingTalentMapper.updateById(leadingApp);
                    logger.info("[提交选房] 已更新领军优青申请表状态为 16，applicationId: {}", applicationId);
                }
            }

            logger.info("[提交选房] 选房成功，houseChoiceId: {}", houseChoice.getId());
            return houseChoice.getId();

        } catch (Exception e) {
            logger.error("[提交选房] 提交失败，userId: {}, error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("提交选房失败：" + e.getMessage());
        }
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
            info.setApplicationId(getApplicationId(latestApp));
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

        private String getApplicationId(Object app) {
            String applicationId = null;

            if (app instanceof ApplicationIndustryTalent) {
                applicationId = ((ApplicationIndustryTalent) app).getApplicationId();
            } else if (app instanceof ApplicationAgencyTalent) {
                applicationId = ((ApplicationAgencyTalent) app).getApplicationId();
            } else if (app instanceof ApplicationLeadingTalent) {
                applicationId = ((ApplicationLeadingTalent) app).getApplicationId();
            }

            return applicationId;
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
  
  @Override
 public List<HouseInfoDTO> queryChooseHouseRecordList(String userId) {
 try {
     logger.info("[查询选房记录列表] 开始查询，userId: {}", userId);
        
       // 1. 从 business_user 获取用户的证件号码
      BusinessUser user = businessUserMapper.selectById(userId);
      if (user == null || user.getIdNumber() == null || user.getIdNumber().isEmpty()) {
         logger.warn("[查询选房记录列表] 用户不存在或证件号码为空，userId: {}", userId);
          return new ArrayList<>();
      }
      
      String choiceZjhm = user.getIdNumber();

       // 2. 从 house_choice 表根据 choice_zjhm 查询所有选房记录
      LambdaQueryWrapper<HouseChoice> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(HouseChoice::getChoiceZjhm, choiceZjhm)
                  .orderByDesc(HouseChoice::getChoiceDate, HouseChoice::getChoiceTime);
      
      List<HouseChoice> houseChoices = houseChoiceMapper.selectList(queryWrapper);
      
      if (houseChoices == null || houseChoices.isEmpty()) {
         logger.info("[查询选房记录列表] 未找到选房记录，userId: {}", userId);
          return new ArrayList<>();
      }
      
     logger.info("[查询选房记录列表] 找到 {} 条选房记录，userId: {}", houseChoices.size(), userId);
        
       // 3. 遍历所有选房记录，查询对应的房屋信息
      List<HouseInfoDTO> resultList = new ArrayList<>();
      for (HouseChoice choice : houseChoices) {
         String houseId = choice.getHouseId();
          
           // 根据 house_id 查询房屋信息（包含小区信息）
         HouseInfoDTO houseInfoDTO = houseInfoMapper.selectHouseInfoById(houseId);
          
         if (houseInfoDTO != null) {
               // 查询户型图图片 URL 列表
            if (houseInfoDTO.getHouseLayoutPicId() != null && !houseInfoDTO.getHouseLayoutPicId().isEmpty()) {
               List<FileInfo> layoutPics = fileInfoMapper.selectByRelId(houseInfoDTO.getHouseLayoutPicId());
               List<String> layoutPicUrls = new ArrayList<>();
               if (layoutPics != null && !layoutPics.isEmpty()) {
                  for(FileInfo pic : layoutPics){
                      layoutPicUrls.add(pic.getFileUrl());
                  }
               }
               houseInfoDTO.setHouseLayoutPicList(layoutPicUrls);
            } else {
               houseInfoDTO.setHouseLayoutPicList(new ArrayList<>());
            }
              
               // 查询实景图图片 URL 列表
            if (houseInfoDTO.getHousePicGroupId() != null && !houseInfoDTO.getHousePicGroupId().isEmpty()) {
               List<FileInfo> housePics = fileInfoMapper.selectByRelId(houseInfoDTO.getHousePicGroupId());
               List<String> housePicUrls = new ArrayList<>();
               if (housePics != null && !housePics.isEmpty()) {
                  for(FileInfo pic : housePics){
                      housePicUrls.add(pic.getFileUrl());
                  }
               }
               houseInfoDTO.setHousePicGroupList(housePicUrls);
            } else {
               houseInfoDTO.setHousePicGroupList(new ArrayList<>());
            }
              
            resultList.add(houseInfoDTO);
         }
      }
      
     logger.info("[查询选房记录列表] 查询成功，userId: {}, 记录数：{}", userId, resultList.size());
     return resultList;
        
    } catch (Exception e) {
     logger.error("[查询选房记录列表] 查询失败，userId: {}, error: {}", userId, e.getMessage(), e);
      return new ArrayList<>();
    }
 }
}
