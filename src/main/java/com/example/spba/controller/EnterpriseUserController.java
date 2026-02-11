package com.example.spba.controller;

import com.example.spba.domain.dto.BusinessUserApproveDTO;
import com.example.spba.domain.dto.BusinessUserUpdateApproveDTO;
import com.example.spba.domain.dto.EnterpriseUserResponseDTO;
import com.example.spba.domain.dto.EnterpriseUsersRequestDTO;
import com.example.spba.service.BusinessEnterpriseService;
import com.example.spba.service.BusinessUserService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
