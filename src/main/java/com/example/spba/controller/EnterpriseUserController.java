package com.example.spba.controller;

import com.example.spba.domain.dto.BusinessUserApproveDTO;
import com.example.spba.domain.dto.BusinessUserUpdateApproveDTO;
import com.example.spba.domain.dto.EnterpriseUserResponseDTO;
import com.example.spba.domain.dto.EnterpriseUsersRequestDTO;
import com.example.spba.domain.dto.EmployeeApplicationApproveDTO;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.domain.entity.BusinessUserDel;
import com.example.spba.domain.entity.HouseUsingJnl;
import com.example.spba.service.ApplicationAgencyService;
import com.example.spba.service.BusinessEnterpriseService;
import com.example.spba.service.BusinessUserService;
import com.example.spba.dao.BusinessUserMapper;
import com.example.spba.dao.BusinessUserDelMapper;
import com.example.spba.dao.HouseUsingJnlMapper;
import com.example.spba.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/enterprise/user")
@Validated
public class EnterpriseUserController {

    @Autowired
    private BusinessEnterpriseService businessEnterpriseService;

    @Autowired
    private BusinessUserService businessUserService;



    /**
     * 获取企业下的用户信息
     * @return 该企业下的用户信息列表
     */
    @PostMapping("/getUserInfo")
    public R getEnterpriseUsers(@RequestAttribute(CURRENT_USER_ID) String userId) {
        try {
            String enterpriseId = userId;

            List<EnterpriseUserResponseDTO> users = businessEnterpriseService.getEnterpriseUsers(enterpriseId);
            return R.success(users);
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("查询用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 审批业务用户（通过/拒绝）
     * @param form
     * @return
     */
    @PostMapping("/register/approve")
    public R approve(@RequestAttribute(CURRENT_USER_ID) String userId, @Validated @RequestBody BusinessUserApproveDTO form)
    {
        try {
            businessUserService.approve(
                    form.getBusinessUserId(), // 这里现在是applyId
                    form.isApproved(),
                    form.getInfo(), userId
            );

            String message = form.isApproved() ? "审批成功" : "已拒绝该注册申请";
            return R.success(message);
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 审批用户信息更新申请（通过/拒绝）
     * @param form
     * @return
     */
    @PostMapping("/update/approve")
    public R updateApprove(@Validated @RequestBody BusinessUserUpdateApproveDTO form)
    {
        try {
            businessUserService.approveUpdate(
                    form.getBusinessUserId(),
                    form.isApproved(),
                    form.getInfo()
            );

            String message = form.isApproved() ? "信息更新审批成功" : "已拒绝信息更新申请";
            return R.success(message);
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 删除员工信息
     * @param param 要删除的员工ID
     * @return 删除结果
     */
    @PostMapping("/del/user")
    @Transactional
    public R deleteUser(@RequestAttribute(CURRENT_USER_ID) String enterpriseId, @RequestBody Map<String, String> param) {
        try {
            String userId = param.get("userId");
            return businessUserService.delUser(userId);
            
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("删除员工信息失败：" + e.getMessage());
        }
    }

    /**
     * 查询企业下所有员工的全部申请记录
     * @return 员工申请记录列表
     */
    @PostMapping("/query/userApply")
    public R queryUserApplications(@RequestAttribute(CURRENT_USER_ID) String enterpriseId) {
        try {
            return businessUserService.queryUserApplications(enterpriseId);
        } catch (Exception e) {
            return R.error("查询员工申请记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 员工申请记录审批接口
     * 实现对员工申请记录的审批，判断企业是产业人才或者机关单位，
     * 是的话根据审批结果修改状态，状态为3-工作单位审核通过；4-工作单位审核拒绝
     * @param form 审批参数
     * @return 审批结果
     */
    @PostMapping("/apply/approve")
    public R approveEmployeeApplication(@RequestAttribute(CURRENT_USER_ID) String enterpriseId,
                                       @Validated(EmployeeApplicationApproveDTO.Approve.class) @RequestBody EmployeeApplicationApproveDTO form) {
        try {
            // 1. 根据申请ID查询申请记录，判断申请类型
            String applicationId = form.getApplicationId();
            
            // 2. 执行审批操作
            if (form.isApproved() != null && form.isApproved()) {
                // 审批通过 - 状态改为工作单位审核通过（3）
                return businessUserService.approveEmployeeApplication(applicationId, 3, form.getRemark(), enterpriseId);
            } else {
                // 审批拒绝 - 状态改为工作单位审核拒绝（4）
                return businessUserService.approveEmployeeApplication(applicationId, 4, form.getRemark(), enterpriseId);
            }
            
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("审批失败：" + e.getMessage());
        }
    }
}