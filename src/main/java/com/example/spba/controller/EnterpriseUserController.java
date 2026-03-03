package com.example.spba.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.spba.domain.dto.BusinessUserApproveDTO;
import com.example.spba.domain.dto.BusinessUserUpdateApproveDTO;
import com.example.spba.domain.dto.EnterpriseUserQueryDTO;
import com.example.spba.domain.dto.EnterpriseUserResponseDTO;
import com.example.spba.domain.dto.EnterpriseUsersRequestDTO;
import com.example.spba.domain.dto.EmployeeApplicationApproveDTO;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.domain.entity.BusinessUserApply;
import com.example.spba.domain.entity.BusinessUserDel;
import com.example.spba.domain.entity.HouseUsingJnl;
import com.example.spba.service.ApplicationAgencyService;
import com.example.spba.service.BusinessEnterpriseService;
import com.example.spba.service.BusinessUserService;
import com.example.spba.dao.BusinessUserMapper;
import com.example.spba.dao.BusinessUserDelMapper;
import com.example.spba.dao.HouseUsingJnlMapper;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseUserController.class);

    @Autowired
    private BusinessEnterpriseService businessEnterpriseService;

    @Autowired
    private BusinessUserService businessUserService;



    /**
     * 获取企业下的用户信息（带筛选条件和分页）
     * @param query 筛选条件和分页参数
     * @return 该企业下的正式用户信息分页列表
     */
    @PostMapping("/getUserInfo")
    public R getEnterpriseUsers(@RequestAttribute(CURRENT_USER_ID) String userId,
                               @RequestBody(required = false) EnterpriseUserQueryDTO query) {
        try {
            logger.info("[企业正式用户查询] 开始查询企业正式用户信息，企业ID: {}, 筛选条件: {}", userId, query);
            String enterpriseId = userId;

            IPage<BusinessUser> users = businessEnterpriseService.getEnterpriseUsers(enterpriseId, query);
            logger.info("[企业正式用户查询] 查询成功，企业ID: {}, 返回用户数: {}, 总记录数: {}", 
                       userId, users.getRecords().size(), users.getTotal());
            return R.success(users);
        } catch (IllegalArgumentException e) {
            logger.error("[企业正式用户查询] 参数异常，企业ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[企业正式用户查询] 查询失败，企业ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("查询正式用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取企业下的用户信息申请（带筛选条件和分页）
     * @param query 筛选条件和分页参数
     * @return 该企业下的用户信息申请分页列表
     */
    @PostMapping("/getUserApplyInfo")
    public R getEnterpriseUsersApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                               @RequestBody(required = false) EnterpriseUserQueryDTO query) {
        try {
            logger.info("[企业用户查询] 开始查询企业用户信息，企业ID: {}, 筛选条件: {}", userId, query);
            String enterpriseId = userId;

            IPage<BusinessUserApply> users = businessEnterpriseService.getEnterpriseUsersApply(enterpriseId, query);
            logger.info("[企业用户查询] 查询成功，企业ID: {}, 返回用户数: {}, 总记录数: {}", 
                       userId, users.getRecords().size(), users.getTotal());
            return R.success(users);
        } catch (IllegalArgumentException e) {
            logger.error("[企业用户查询] 参数异常，企业ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[企业用户查询] 查询失败，企业ID: {}, 异常: {}", userId, e.getMessage(), e);
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
            logger.info("[用户注册审批] 开始审批用户注册，审批人ID: {}, 申请ID: {}, 审批结果: {}, 审批意见: {}", 
                       userId, form.getApplyId(), form.isApproved() ? "通过" : "拒绝", form.getInfo());
            
            businessUserService.approve(
                    form.getApplyId(), // 这里现在是applyId
                    form.isApproved(),
                    form.getInfo(), userId
            );

            String message = form.isApproved() ? "审批成功" : "已拒绝该注册申请";
            logger.info("[用户注册审批] 审批完成，审批人ID: {}, 申请ID: {}, 结果: {}", userId, form.getApplyId(), message);
            return R.success(message);
        } catch (IllegalArgumentException e) {
            logger.error("[用户注册审批] 参数异常，审批人ID: {}, 申请ID: {}, 异常: {}", userId, form.getApplyId(), e.getMessage(), e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[用户注册审批] 审批失败，审批人ID: {}, 申请ID: {}, 异常: {}", userId, form.getApplyId(), e.getMessage(), e);
            return R.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 审批用户信息更新申请（通过/拒绝）
     * @param form
     * @return
     */
    @PostMapping("/update/approve")
    public R updateApprove(@RequestAttribute(CURRENT_USER_ID) String userId, @Validated @RequestBody BusinessUserUpdateApproveDTO form)
    {
        try {
            logger.info("[用户信息更新审批] 开始审批用户信息更新，申请ID: {}, 审批结果: {}, 审批意见: {}", 
                       form.getApplyId(), form.isApproved() ? "通过" : "拒绝", form.getInfo());
            
            businessUserService.approveUpdate(
                    form.getApplyId(),
                    form.isApproved(),
                    form.getInfo(),
                    userId
            );

            String message = form.isApproved() ? "信息更新审批成功" : "已拒绝信息更新申请";
            logger.info("[用户信息更新审批] 审批完成，申请ID: {}, 结果: {}", form.getApplyId(), message);
            return R.success(message);
        } catch (IllegalArgumentException e) {
            logger.warn("[用户信息更新审批] 参数异常，申请ID: {}, 异常: {}", form.getApplyId(), e.getMessage());
            return R.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[用户信息更新审批] 审批失败，申请ID: {}, 异常: {}", form.getApplyId(), e.getMessage(), e);
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
            logger.info("[删除员工] 开始删除员工信息，企业ID: {}, 员工ID: {}", enterpriseId, userId);
            
            R result = businessUserService.delUser(userId);
            logger.info("[删除员工] 删除完成，企业ID: {}, 员工ID: {}, 结果: {}", enterpriseId, userId, result.getMessage());
            return result;
            
        } catch (IllegalArgumentException e) {
            logger.warn("[删除员工] 参数异常，企业ID: {}, 员工ID: {}, 异常: {}", enterpriseId, param.get("userId"), e.getMessage());
            return R.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[删除员工] 删除失败，企业ID: {}, 员工ID: {}, 异常: {}", enterpriseId, param.get("userId"), e.getMessage(), e);
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
            logger.info("[查询员工申请记录] 开始查询员工申请记录，企业ID: {}", enterpriseId);
            
            R result = businessUserService.queryUserApplications(enterpriseId);
            logger.info("[查询员工申请记录] 查询完成，企业ID: {}, 结果: {}", enterpriseId, result.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("[查询员工申请记录] 查询失败，企业ID: {}, 异常: {}", enterpriseId, e.getMessage(), e);
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
            logger.info("[员工申请审批] 开始审批员工申请，企业ID: {}, 申请ID: {}, 审批结果: {}, 审批备注: {}", 
                       enterpriseId, form.getApplicationId(), 
                       form.isApproved() != null && form.isApproved() ? "通过" : "拒绝", 
                       form.getRemark());
            
            // 1. 根据申请ID查询申请记录，判断申请类型
            String applicationId = form.getApplicationId();
            
            // 2. 执行审批操作
            R result;
            if (form.isApproved() != null && form.isApproved()) {
                // 审批通过 - 状态改为工作单位审核通过（3）
                logger.info("[员工申请审批] 审批通过，申请ID: {}, 新状态: 工作单位审核通过(3)", applicationId);
                result = businessUserService.approveEmployeeApplication(applicationId, 3, form.getRemark(), enterpriseId);
            } else {
                // 审批拒绝 - 状态改为工作单位审核拒绝（4）
                logger.info("[员工申请审批] 审批拒绝，申请ID: {}, 新状态: 工作单位审核拒绝(4)", applicationId);
                result = businessUserService.approveEmployeeApplication(applicationId, 4, form.getRemark(), enterpriseId);
            }
            
            logger.info("[员工申请审批] 审批完成，企业ID: {}, 申请ID: {}, 结果: {}", enterpriseId, applicationId, result.getMessage());
            return result;
            
        } catch (IllegalArgumentException e) {
            logger.warn("[员工申请审批] 参数异常，企业ID: {}, 申请ID: {}, 异常: {}", enterpriseId, form.getApplicationId(), e.getMessage());
            return R.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[员工申请审批] 审批失败，企业ID: {}, 申请ID: {}, 异常: {}", enterpriseId, form.getApplicationId(), e.getMessage(), e);
            return R.error("审批失败：" + e.getMessage());
        }
    }
}