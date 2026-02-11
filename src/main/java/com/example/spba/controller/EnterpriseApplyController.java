package com.example.spba.controller;

import com.example.spba.controller.base.BaseController;
import com.example.spba.dao.ProjectCommunityMapper;
import com.example.spba.domain.dto.EnterpriseSubmitDTO;
import com.example.spba.domain.dto.EnterpriseUpdateDTO;
import com.example.spba.service.EnterpriseApplyService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USERNAME;
import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/enterprise/apply")
@Validated
public class EnterpriseApplyController extends BaseController {

    @Resource
    private EnterpriseApplyService enterpriseApplyService;
    
    @Autowired
    private ProjectCommunityMapper projectCommunityMapper;

//    /**
//     * 新增标签接口
//     * @param tag 申请标签
//     * @param title 标签名称
//     * @param files 图片文件列表
//     * @return 操作结果
//     */
//    @PostMapping(value = "/addTag", consumes = "multipart/form-data")
//    public R addTag(@RequestParam("tag") String tag,
//                    @RequestParam("title") String title,
//                    @RequestPart("files") List<MultipartFile> files) {
//        // 可以在这里获取当前用户ID进行权限验证或日志记录
//        String currentUserId = getCurrentUserId();
//        System.out.println("当前操作用户ID: " + currentUserId);
//
//        return enterpriseApplyService.addTag(tag, title, files);
//    }
    
    /**
     * 企业申请提交接口
     * @param submitDTO 申请信息
     * @return 操作结果
     */
    @PostMapping("/submit")
    public R submitApplication(@RequestAttribute(CURRENT_USER_ID) String userId, @RequestAttribute(CURRENT_USERNAME) String userName, @Valid @RequestBody EnterpriseSubmitDTO submitDTO) {

        return enterpriseApplyService.submitApplication(submitDTO, userId, userName);
    }


    
    /**
     * 查询企业申请视图信息
     * @return 企业申请视图列表，包含企业名称和小区名称等关联信息
     */
    @PostMapping("/query")
    public R queryViewApplications() {
        // 可以根据当前用户ID进行数据过滤
        String currentUserId = getCurrentUserId();
        System.out.println("查询申请的用户ID: " + currentUserId);
        
        return enterpriseApplyService.queryViewApplications();
    }
    
    /**
     * 企业申请撤回接口
     * @param  param 申请ID
     * @return 操作结果
     */
    @PostMapping("/withdraw")
    public R withdrawApplication(@RequestAttribute(CURRENT_USER_ID) String userId, @RequestBody Map<String,String> param) {
        // 获取当前用户ID，用于权限验证
        String applicationId = param.get("applicationId");
        return enterpriseApplyService.withdrawApplication(applicationId, userId);
    }
    
    /**
     * 查询项目下的小区信息
     * @param param 包含项目ID的参数
     * @return 该项目关联的小区ID列表
     */
    @PostMapping("/query/community")
    public R queryCommunitiesByProject(@RequestBody Map<String, String> param) {
        try {
            String projectId = param.get("projectId");
            
            // 参数校验
            if (projectId == null || projectId.trim().isEmpty()) {
                return R.error("项目ID不能为空");
            }
            
            // 查询该项目关联的小区列表
            List<String> communityIds = projectCommunityMapper.selectCommunityIdsByProjectId(projectId);
            
            return R.success(communityIds);
        } catch (Exception e) {
            return R.error("查询小区信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 企业申请修改接口
     * 只有撤回状态(apply_status=0)的申请才能修改
     * @param submitDTO 包含申请ID和修改信息的参数
     * @return 操作结果
     */
    @PostMapping("/update/submit")
    public R updateApplication(@RequestAttribute(CURRENT_USER_ID) String userId, 
                              @RequestAttribute(CURRENT_USERNAME) String userName,
             @Valid @RequestBody EnterpriseUpdateDTO submitDTO) {
        try {
            // 获取申请ID
            String applicationId = submitDTO.getApplicationId();
            if (applicationId == null || applicationId.trim().isEmpty()) {
                return R.error("申请ID不能为空");
            }
            return enterpriseApplyService.updateApplication(submitDTO, userId, userName);
        } catch (Exception e) {
            return R.error("申请修改失败: " + e.getMessage());
        }
    }
}