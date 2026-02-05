package com.example.spba.controller;

import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.dto.EnterpriseSubmitDTO;
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

@RestController
@RequestMapping("/enterprise/apply")
@Validated
public class EnterpriseApplyController extends BaseController {

    @Resource
    private EnterpriseApplyService enterpriseApplyService;

    /**
     * 新增标签接口
     * @param tag 申请标签
     * @param title 标签名称
     * @param files 图片文件列表
     * @return 操作结果
     */
    @PostMapping(value = "/addTag", consumes = "multipart/form-data")
    public R addTag(@RequestParam("tag") String tag,
                    @RequestParam("title") String title,
                    @RequestPart("files") List<MultipartFile> files) {
        // 可以在这里获取当前用户ID进行权限验证或日志记录
        String currentUserId = getCurrentUserId();
        System.out.println("当前操作用户ID: " + currentUserId);
        
        return enterpriseApplyService.addTag(tag, title, files);
    }
    
    /**
     * 企业申请提交接口
     * @param submitDTO 申请信息
     * @return 操作结果
     */
    @PostMapping("/submit")
    public R submitApplication(@Valid @RequestBody EnterpriseSubmitDTO submitDTO) {
        // 获取当前用户ID，可以在服务层使用
        String currentUserId = getCurrentUserId();
        System.out.println("提交申请的用户ID: " + currentUserId);
        
        return enterpriseApplyService.submitApplication(submitDTO);
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
    public R withdrawApplication(@RequestBody Map<String,String> param) {
        // 获取当前用户ID，用于权限验证
        String applicationId = param.get("applicationId");
        return enterpriseApplyService.withdrawApplication(applicationId);
    }

}