package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.*;
import com.example.spba.domain.dto.HouseInfoDTO;
import com.example.spba.domain.dto.HouseInfoQueryDTO;
import com.example.spba.domain.dto.KeyValueItemDTO;
import com.example.spba.domain.dto.ProjectInfoDTO;
import com.example.spba.domain.dto.CommunityCarouselDTO;
import com.example.spba.domain.dto.CommunityDetailDTO;
import com.example.spba.domain.dto.PortalContentDTO;
import com.example.spba.domain.entity.*;
import com.example.spba.service.PublicService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicServiceImpl implements PublicService {

    private static final Logger logger = LoggerFactory.getLogger(PublicServiceImpl.class);

    @Autowired
    private PublicParametersMapper publicParametersMapper;

    @Autowired
    private BusinessEnterpriseMapper businessEnterpriseMapper;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private ProjectCommunityMapper projectCommunityMapper;

    @Autowired
    private CommunityInfoMapper communityInfoMapper;

    @Autowired
    private HouseInfoMapper houseInfoMapper;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private InfoPortalContentMapper infoPortalContentMapper;

    @Override
    public List<Map<String, String>> convertKeyValueToValueText(Map<String, String> keyValueMap) {
        List<Map<String, String>> result = new ArrayList<>();

        if (keyValueMap != null && !keyValueMap.isEmpty()) {
            for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
                Map<String, String> item = new HashMap<>();
                item.put("value", entry.getKey());
                item.put("text", entry.getValue());
                result.add(item);
            }
        }

        return result;
    }

    @Override
    public List<KeyValueItemDTO> convertKeyValueToDTO(Map<String, String> keyValueMap) {
        List<KeyValueItemDTO> result = new ArrayList<>();

        if (keyValueMap != null && !keyValueMap.isEmpty()) {
            for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
                KeyValueItemDTO item = new KeyValueItemDTO(entry.getKey(), entry.getValue());
                result.add(item);
            }
        }

        return result;
    }

    @Override
    public R queryCountry() {
        try {
            logger.info("[查询国家信息] 开始查询国家信息");

            List<String> countryList = publicParametersMapper.selectByType("country");
            logger.info("[查询国家信息] 查询完成，返回国家数量: {}", countryList.size());

            return R.success(countryList);
        } catch (Exception e) {
            logger.error("[查询国家信息] 查询失败，异常: {}", e.getMessage(), e);
            return R.error("查询国家信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryEnterprise() {
        try {
            logger.info("[查询企业信息] 开始查询企业信息");

            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("reg_category", 1);
            List<BusinessEnterprise> enterpriseList = businessEnterpriseMapper.selectList(queryWrapper);

            Map<String, String> enterpriseMap = new HashMap<>();
            for (BusinessEnterprise enterprise : enterpriseList) {
                enterpriseMap.put(enterprise.getId(), enterprise.getEnterpriseName());
            }

            logger.info("[查询企业信息] 查询完成，返回企业数量: {}", enterpriseList.size());
            return R.success(this.convertKeyValueToValueText(enterpriseMap));
        } catch (Exception e) {
            logger.error("[查询企业信息] 查询失败，异常: {}", e.getMessage(), e);
            return R.error("查询企业信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryUserProject() {
        try {
            logger.info("[查询项目信息] 开始查询项目信息");

            QueryWrapper<ProjectInfo> queryWrapper = new QueryWrapper<>();

            //获取当前时间 格式yyyyMMdd hh:mm:ss
            String currentDate = Time.getNowTimeDate("yyyyMMdd HH:mm:ss");

            queryWrapper.lt("apply_start_time", currentDate);
            queryWrapper.gt("apply_end_time", currentDate);

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

            logger.info("[查询项目信息] 查询完成，返回项目数量: {}", projectList.size());
            return R.success(result);

        } catch (Exception e) {
            logger.error("[查询项目信息] 查询失败，异常: {}", e.getMessage(), e);
            return R.error("查询项目信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryEnterpriseProject() {
        try {
            logger.info("[查询项目信息] 开始查询项目信息");

            QueryWrapper<ProjectInfo> queryWrapper = new QueryWrapper<>();

            //获取当前时间 格式yyyyMMdd hh:mm:ss
            String currentDate = Time.getNowTimeDate("yyyyMMdd HH:mm:ss");

            queryWrapper.lt("enterprise_start_time", currentDate);
            queryWrapper.gt("enterprise_end_time", currentDate);

            // 只查询开启状态的项目
            queryWrapper.eq("status", 1);

            // 按申请开始时间升序排列
            queryWrapper.orderByAsc("enterprise_start_time");

            List<ProjectInfo> projectList = projectInfoMapper.selectList(queryWrapper);

            // 转换为DTO格式
            List<ProjectInfoDTO> result = new ArrayList<>();
            for (ProjectInfo project : projectList) {
                ProjectInfoDTO dto = new ProjectInfoDTO();
                dto.setId(project.getId());
                dto.setProjectName(project.getProjectName());
                result.add(dto);
            }

            logger.info("[查询项目信息] 查询完成，返回项目数量: {}", projectList.size());
            if (result.size() == 0) {
                return R.error(555, "当前非申请时段，请在申请时段提交申请。");
            }
            return R.success(result);

        } catch (Exception e) {
            logger.error("[查询项目信息] 查询失败，异常: {}", e.getMessage(), e);
            return R.error("查询项目信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryEnterpriseLocation() {
        try {
            logger.info("[查询企业属地信息] 开始查询企业属地信息");

            // 查询parent_id为200的部门信息
            QueryWrapper<SysDept> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id", 200);
            List<SysDept> deptList = sysDeptMapper.selectList(queryWrapper);

            // 转换为需要的格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (SysDept dept : deptList) {
                Map<String, Object> deptInfo = new HashMap<>();
                deptInfo.put("dept_id", dept.getDeptId());
                deptInfo.put("dept_name", dept.getDeptName());
                result.add(deptInfo);
            }

            logger.info("[查询企业属地信息] 查询完成，返回属地数量: {}", deptList.size());
            return R.success(result);
        } catch (Exception e) {
            logger.error("[查询企业属地信息] 查询失败，异常: {}", e.getMessage(), e);
            return R.error("查询企业属地信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryCommunitiesByProject(String projectId) {
        logger.info("[查询项目小区] 开始查询项目关联的小区信息，项目ID: {}", projectId);

        try {
            // 参数校验
            if (projectId == null || projectId.trim().isEmpty()) {
                logger.error("[查询项目小区] 项目ID为空");
                return R.error("项目ID不能为空");
            }

            // 查询该项目关联的小区ID列表
            List<String> communityIds = projectCommunityMapper.selectCommunityIdsByProjectId(projectId);
            logger.info("[查询项目小区] 查询到小区ID数量: {}", communityIds.size());

            if (communityIds.isEmpty()) {
                logger.info("[查询项目小区] 项目ID: {} 未关联任何小区", projectId);
                return R.success(new ArrayList<>());
            }

            // 根据小区ID查询小区名称
            List<CommunityInfo> communityInfos = communityInfoMapper.selectByIds(communityIds);

            // 构造返回结果：包含 value(小区 id) 和 text(小区名称)
            List<Map<String, String>> result = new ArrayList<>();
            for (CommunityInfo community : communityInfos) {
                Map<String, String> communityData = new HashMap<>();
                communityData.put("value", community.getId());
                communityData.put("text", community.getCommunityName());
                result.add(communityData);
            }

            logger.info("[查询项目小区] 查询完成，项目ID: {}, 返回小区数量: {}", projectId, result.size());
            return R.success(result);

        } catch (Exception e) {
            logger.error("[查询项目小区] 查询失败，项目ID: {}, 异常: {}", projectId, e.getMessage(), e);
            return R.error("查询小区信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryPublicHouseInfoList(HouseInfoQueryDTO queryDTO) {
        try {
            logger.info("[公共查询房屋信息] 开始查询房屋信息列表，page: {}, size: {}",
                    queryDTO.getPage(), queryDTO.getSize());

            // 设置默认分页参数
            if (queryDTO.getPage() == null || queryDTO.getPage() < 1) {
                queryDTO.setPage(1);
            }
            if (queryDTO.getSize() == null || queryDTO.getSize() < 1) {
                queryDTO.setSize(10);
            }

            // 调用 Mapper 查询全部数据
            List<HouseInfoDTO> allList = houseInfoMapper.selectPublicHouseInfoList(queryDTO);
            logger.info("[公共查询房屋信息] 查询完成，共找到 {} 条记录", allList.size());

            // 计算总数
            int total = allList.size();

            // 手动分页
            int page = queryDTO.getPage();
            int size = queryDTO.getSize();
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, total);

            List<HouseInfoDTO> pageList = new ArrayList<>();
            if (fromIndex < total) {
                pageList = allList.subList(fromIndex, toIndex);
            }

            // 为每套房子填充图片 URL 列表
            for (HouseInfoDTO houseInfo : pageList) {
                // 查询户型图图片 URL 列表
                if (houseInfo.getHouseLayoutPicId() != null && !houseInfo.getHouseLayoutPicId().isEmpty()) {
                    List<FileInfo> layoutPics = fileInfoMapper.selectByRelId(houseInfo.getHouseLayoutPicId());
                    List<String> layoutPicUrls = new ArrayList<>();
                    if (layoutPics != null && !layoutPics.isEmpty()) {
                        for (FileInfo pic : layoutPics) {
                            layoutPicUrls.add(pic.getFileUrl());
                        }
                    }
                    houseInfo.setHouseLayoutPicList(layoutPicUrls);
                } else {
                    houseInfo.setHouseLayoutPicList(new ArrayList<>());
                }

                // 查询实景图图片 URL 列表
                if (houseInfo.getHousePicGroupId() != null && !houseInfo.getHousePicGroupId().isEmpty()) {
                    List<FileInfo> housePics = fileInfoMapper.selectByRelId(houseInfo.getHousePicGroupId());
                    List<String> housePicUrls = new ArrayList<>();
                    if (housePics != null && !housePics.isEmpty()) {
                        for (FileInfo pic : housePics) {
                            housePicUrls.add(pic.getFileUrl());
                        }
                    }
                    houseInfo.setHousePicGroupList(housePicUrls);
                } else {
                    houseInfo.setHousePicGroupList(new ArrayList<>());
                }
            }

            // 构造分页响应数据
            Map<String, Object> result = new HashMap<>();
            result.put("records", pageList);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (int) Math.ceil((double) total / size));

            logger.info("[公共查询房屋信息] 查询成功，返回 {} 条记录，总记录数：{}，页码：{}，每页大小：{}",
                    pageList.size(), total, page, size);
            return R.success(result);

        } catch (Exception e) {
            logger.error("[公共查询房屋信息] 查询失败，异常：{}", e.getMessage(), e);
            return R.error("查询房屋信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryCommunityCarouselList() {
        try {
            logger.info("[查询小区轮播图] 开始查询小区轮播图列表");

            // 1. 查询所有有效的小区（delete_status = '0'）
            QueryWrapper<CommunityInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("delete_status", "0")
//                    .isNotNull("community_pic_group_id")
//                    .ne("community_pic_group_id", "")
                    .orderByDesc("last_update_date", "last_update_time");

            List<CommunityInfo> communities = communityInfoMapper.selectList(queryWrapper);
            logger.info("[查询小区轮播图] 查询到 {} 个小区", communities.size());

            if (communities == null || communities.isEmpty()) {
                logger.info("[查询小区轮播图] 未找到小区数据");
                return R.success(new ArrayList<>());
            }

            // 2. 构建返回结果
            List<CommunityCarouselDTO> resultList = new ArrayList<>();
            for (CommunityInfo community : communities) {
                CommunityCarouselDTO dto = new CommunityCarouselDTO();
                dto.setCommunityId(community.getId());
                dto.setCommunityName(community.getCommunityName());

                // 3. 查询小区图片，取第一张
                if (community.getCommunityPicGroupId() != null && !community.getCommunityPicGroupId().isEmpty()) {
                    List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getCommunityPicGroupId());
                    if (pics != null && !pics.isEmpty()) {
                        dto.setCommunityPic(pics.get(0).getFileUrl());
                    } else {
                        dto.setCommunityPic("");
                    }
                } else {
                    dto.setCommunityPic("");
                }

                resultList.add(dto);
            }

            logger.info("[查询小区轮播图] 查询成功，返回 {} 条记录", resultList.size());
            return R.success(resultList);

        } catch (Exception e) {
            logger.error("[查询小区轮播图] 查询失败，异常：{}", e.getMessage(), e);
            return R.error("查询小区轮播图失败：" + e.getMessage());
        }
    }

    @Override
    public R queryCommunityDetail(String communityId) {
        try {
            logger.info("[获取小区详细信息] 开始查询，communityId: {}", communityId);

            // 1. 参数校验
            if (communityId == null || communityId.isEmpty()) {
                logger.warn("[获取小区详细信息] 小区 ID 为空");
                return R.error("小区 ID 不能为空");
            }

            // 2. 查询小区信息
            CommunityInfo community = communityInfoMapper.selectById(communityId);
            if (community == null) {
                logger.warn("[获取小区详细信息] 小区不存在，communityId: {}", communityId);
                return R.error("小区不存在");
            }

            // 3. 构建返回结果
            CommunityDetailDTO detail = new CommunityDetailDTO();
            detail.setCommunityName(community.getCommunityName());
            detail.setHouseDesc(community.getCommunityDesc());
            detail.setCommunityLocation(community.getCommunityLocation());
            detail.setTag1(community.getTag1());
            detail.setTag2(community.getTag2());
            detail.setTag3(community.getTag3());
            detail.setHasZeroBedroom(community.getHasZeroBedroom());
            detail.setHasOneBedroom(community.getHasOneBedroom());
            detail.setHasTwoBedroom(community.getHasTwoBedroom());
            detail.setHasThreeBedroom(community.getHasThreeBedroom());

            // 4. 查询小区图片列表
            if (community.getCommunityPicGroupId() != null && !community.getCommunityPicGroupId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getCommunityPicGroupId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setCommunityPicList(picUrls);
            } else {
                detail.setCommunityPicList(new ArrayList<>());
            }

            // 5. 查询开间户型图和实景图
            if (community.getZerobedroomLayoutPicId() != null && !community.getZerobedroomLayoutPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getZerobedroomLayoutPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setZerobedroomLayoutPicList(picUrls);
            } else {
                detail.setZerobedroomLayoutPicList(new ArrayList<>());
            }

            if (community.getZerobedroomPhotoPicId() != null && !community.getZerobedroomPhotoPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getZerobedroomPhotoPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setZerobedroomPhotoPicList(picUrls);
            } else {
                detail.setZerobedroomPhotoPicList(new ArrayList<>());
            }

            // 6. 查询一居户型图和实景图
            if (community.getOnebedroomLayoutPicId() != null && !community.getOnebedroomLayoutPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getOnebedroomLayoutPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setOnebedroomLayoutPicList(picUrls);
            } else {
                detail.setOnebedroomLayoutPicList(new ArrayList<>());
            }

            if (community.getOnebedroomPhotoPicId() != null && !community.getOnebedroomPhotoPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getOnebedroomPhotoPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setOnebedroomPhotoPicList(picUrls);
            } else {
                detail.setOnebedroomPhotoPicList(new ArrayList<>());
            }

            // 7. 查询二居户型图和实景图
            if (community.getTwobedroomLayoutPicId() != null && !community.getTwobedroomLayoutPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getTwobedroomLayoutPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setTwobedroomLayoutPicList(picUrls);
            } else {
                detail.setTwobedroomLayoutPicList(new ArrayList<>());
            }

            if (community.getTwobedroomPhotoPicId() != null && !community.getTwobedroomPhotoPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getTwobedroomPhotoPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setTwobedroomPhotoPicList(picUrls);
            } else {
                detail.setTwobedroomPhotoPicList(new ArrayList<>());
            }

            // 8. 查询三居户型图和实景图
            if (community.getThreebedroomLayoutPicId() != null && !community.getThreebedroomLayoutPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getThreebedroomLayoutPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setThreebedroomLayoutPicList(picUrls);
            } else {
                detail.setThreebedroomLayoutPicList(new ArrayList<>());
            }

            if (community.getThreebedroomPhotoPicId() != null && !community.getThreebedroomPhotoPicId().isEmpty()) {
                List<FileInfo> pics = fileInfoMapper.selectByRelId(community.getThreebedroomPhotoPicId());
                List<String> picUrls = new ArrayList<>();
                if (pics != null && !pics.isEmpty()) {
                    for (FileInfo pic : pics) {
                        picUrls.add(pic.getFileUrl());
                    }
                }
                detail.setThreebedroomPhotoPicList(picUrls);
            } else {
                detail.setThreebedroomPhotoPicList(new ArrayList<>());
            }

            // 9. 构造返回结果（列表格式）
            List<CommunityDetailDTO> resultList = new ArrayList<>();
            resultList.add(detail);

            logger.info("[获取小区详细信息] 查询成功，communityId: {}", communityId);
            return R.success(resultList);

        } catch (Exception e) {
            logger.error("[获取小区详细信息] 查询失败，communityId: {}, 异常：{}", communityId, e.getMessage(), e);
            return R.error("获取小区详细信息失败：" + e.getMessage());
        }
    }

    @Override
    public R queryPortalContent() {
        try {
            logger.info("[获取通知公告] 开始查询通知公告详细信息");

            // 1. 查询 section_id 为'1'的数据
            QueryWrapper<InfoPortalContent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("section_id", "1")
                    .eq("del_status", 0)  // 未删除
                    .eq("status", 1)       // 已发布
                    .orderByDesc("publish_date", "publish_time");

            List<InfoPortalContent> contentList = infoPortalContentMapper.selectList(queryWrapper);
            
            if (contentList == null || contentList.isEmpty()) {
                logger.info("[获取通知公告] 未找到通知公告数据");
                return R.success(new ArrayList<>());
            }

            // 2. 构建返回结果列表
            List<PortalContentDTO> resultList = new ArrayList<>();
            for (InfoPortalContent content : contentList) {
                PortalContentDTO result = new PortalContentDTO();
                result.setPublishDate(content.getPublishDate());
                result.setPublishTime(content.getPublishTime());
                result.setTitle(content.getTitle());
                resultList.add(result);
            }

            logger.info("[获取通知公告] 查询成功，共找到 {} 条记录", resultList.size());
            return R.success(resultList);

        } catch (Exception e) {
            logger.error("[获取通知公告] 查询失败，异常：{}", e.getMessage(), e);
            return R.error("获取通知公告失败：" + e.getMessage());
        }
    }

    @Override
    public R queryPortalContentById(Integer id) {
        try {
            logger.info("[获取通知公告详情] 开始查询，id: {}", id);

            // 1. 参数校验
            if (id == null || id <= 0) {
                logger.warn("[获取通知公告详情] ID 不合法，id: {}", id);
                return R.error("ID 不合法");
            }

            // 2. 查询通知公告详细信息
            InfoPortalContent content = infoPortalContentMapper.selectById(id);
            
            if (content == null) {
                logger.info("[获取通知公告详情] 未找到通知公告数据，id: {}", id);
                return R.success(null);
            }

            // 3. 构建返回结果
            PortalContentDTO result = new PortalContentDTO();
            result.setPublishDate(content.getPublishDate());
            result.setPublishTime(content.getPublishTime());
            result.setTitle(content.getTitle());
            result.setContent(content.getContent());

            logger.info("[获取通知公告详情] 查询成功，id: {}, title: {}", id, content.getTitle());
            return R.success(result);

        } catch (Exception e) {
            logger.error("[获取通知公告详情] 查询失败，id: {}, 异常：{}", id, e.getMessage(), e);
            return R.error("获取通知公告详情失败：" + e.getMessage());
        }
    }
}