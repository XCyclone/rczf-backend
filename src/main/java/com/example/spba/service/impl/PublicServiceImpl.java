package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.*;
import com.example.spba.domain.dto.HouseInfoDTO;
import com.example.spba.domain.dto.HouseInfoQueryDTO;
import com.example.spba.domain.dto.KeyValueItemDTO;
import com.example.spba.domain.dto.ProjectInfoDTO;
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
}