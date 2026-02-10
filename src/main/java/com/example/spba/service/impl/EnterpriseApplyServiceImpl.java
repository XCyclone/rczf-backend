package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.ApplicationIndustryTalentMapper;
import com.example.spba.dao.ApplicationTagInfoMapper;
import com.example.spba.dao.FileInfoMapper;
import com.example.spba.dao.ViewApplicationIndustryMapper;
import com.example.spba.domain.dto.EnterpriseSubmitDTO;
import com.example.spba.domain.entity.ApplicationIndustryTalent;
import com.example.spba.domain.entity.ApplicationTagInfo;
import com.example.spba.domain.entity.FileInfo;
import com.example.spba.domain.entity.ViewApplicationIndustry;
import com.example.spba.service.EnterpriseApplyService;
import com.example.spba.utils.FileUploadUtil;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class EnterpriseApplyServiceImpl implements EnterpriseApplyService {

    @Autowired
    private ApplicationIndustryTalentMapper applicationIndustryTalentMapper;
    
    @Autowired
    private ApplicationTagInfoMapper applicationTagInfoMapper;

    @Autowired
    private FileInfoMapper fileInfoMapper;
    
    @Autowired
    private ViewApplicationIndustryMapper viewApplicationIndustryMapper;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Override
    public R addTag(String tag, String title, List<MultipartFile> files) {
        try {
            // 验证必填参数
            if (tag == null || tag.trim().isEmpty()) {
                return R.error("申请标签不能为空");
            }
            if (title == null || title.trim().isEmpty()) {
                return R.error("标签名称不能为空");
            }
            if (files == null || files.isEmpty()) {
                return R.error("至少需要上传一个文件");
            }

            // 验证企业申请是否存在
            // 这里可以根据实际需求添加对企业申请存在的验证逻辑

            // 处理上传的文件
            for (MultipartFile file : files) {
                // 生成标签信息ID
                String tagInfoId = UUID.randomUUID().toString().replace("-", "");

                // 创建标签信息记录
                ApplicationTagInfo tagInfo = new ApplicationTagInfo();
                tagInfo.setId(tagInfoId);
                tagInfo.setTag(tag);
                tagInfo.setTitle(title);

                // 保存标签信息记录
                applicationTagInfoMapper.insert(tagInfo);

                // 生成文件ID
                String fileId = UUID.randomUUID().toString().replace("-", "");

                try {
                    // 使用文件上传工具处理文件上传
                    FileUploadUtil.UploadedFileInfo uploadedFile = fileUploadUtil.uploadFile(file, "file_info");
                    
                    String fileName = uploadedFile.getOriginalName();
                    String storedFileName = uploadedFile.getStoredName();
                    String filePath = uploadedFile.getFilePath();
                    String fileUrl = "/profile/upload/file_info/" + storedFileName;
                    // 创建文件信息记录，relationId指向标签信息ID
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setId(fileId);
                    fileInfo.setRelationId(tagInfoId); // 关联到标签信息ID
                    fileInfo.setOriginalName(fileName);
                    fileInfo.setStoredName(storedFileName);
                    fileInfo.setFilePath(filePath);
                    fileInfo.setFileUrl(fileUrl);
                    fileInfo.setFileType(file.getContentType());
                    fileInfo.setLastUpdateDate(Time.getNowTimeDate("yyyy-MM-dd"));
                    fileInfo.setLastUpdateTime(Time.getNowTimeDate("HH:mm:ss"));
                    fileInfo.setLastUpdater(""); // 可以根据实际需求设置操作人

                    // 保存文件信息记录
                    fileInfoMapper.insert(fileInfo);
                } catch (Exception e) {
                    return R.error("文件上传失败: " + e.getMessage());
                }


            }

            return R.success("标签添加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return R.error("标签添加失败: " + e.getMessage());
        }
    }
    
    @Override
    public R submitApplication(EnterpriseSubmitDTO submitDTO) {
        try {
            // 生成申请ID
            String applicationId = UUID.randomUUID().toString().replace("-", "");
            
            // 创建申请记录
            ApplicationIndustryTalent application = new ApplicationIndustryTalent();
            application.setApplicationId(applicationId);
            application.setProjectId(submitDTO.getProjectId());
            application.setProjectName(submitDTO.getProjectName());
            application.setCommunityId(submitDTO.getCommunityId());
            application.setHouseCount(submitDTO.getHouseCount());
            application.setExpectedMoveinDate(submitDTO.getExpectedMoveinDate());
            application.setApplyDate(Time.getNowTimeDate("yyyy-MM-dd"));
            application.setApplyTime(Time.getNowTimeDate("HH:mm:ss"));
            application.setApplyStatus(1); // 提交/待审核状态
            
            // 保存申请记录
            applicationIndustryTalentMapper.insert(application);
            
            // 更新标签信息中的application_id
            if (submitDTO.getTagIds() != null && !submitDTO.getTagIds().isEmpty()) {
                QueryWrapper<ApplicationTagInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.in("id", submitDTO.getTagIds());
                List<ApplicationTagInfo> tagInfos = applicationTagInfoMapper.selectList(queryWrapper);
                
                for (ApplicationTagInfo tagInfo : tagInfos) {
                    tagInfo.setApplicationId(applicationId);
                    applicationTagInfoMapper.updateById(tagInfo);
                }
            }
            
            return R.success("企业申请提交成功", applicationId);
            
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("企业申请提交失败: " + e.getMessage());
        }
    }

    @Override
    public R queryViewApplications() {
        try {
            // 查询视图中的企业申请记录
            List<ViewApplicationIndustry> applications = viewApplicationIndustryMapper.selectList(null);
            return R.success(applications);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询企业申请视图失败: " + e.getMessage());
        }
    }
    
    @Override
    public R withdrawApplication(String applicationId) {
        try {
            // 参数验证
            if (applicationId == null || applicationId.trim().isEmpty()) {
                return R.error("申请ID不能为空");
            }
            
            // 查询申请记录
            ApplicationIndustryTalent application = applicationIndustryTalentMapper.selectById(applicationId);
            if (application == null) {
                return R.error("未找到对应的企业申请记录");
            }
            
            // 检查申请状态，只有状态为1（待审核）的记录才能撤回
            if (application.getApplyStatus() != 1) {
                return R.error("只有待审核状态的申请才能撤回");
            }
            
            // 更新申请状态为0（撤回/待提交）
            application.setApplyStatus(0);
            applicationIndustryTalentMapper.updateById(application);
            
            return R.success("申请撤回成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("申请撤回失败: " + e.getMessage());
        }
    }
}