package com.example.spba.controller;

import com.example.spba.service.ChooseHouseService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/choose/house")
public class ChooseHouseController {
    
    @Autowired
    private ChooseHouseService chooseHouseService;
    
    /**
     * 判断当前员工用户是否在选房时间内
     * @param userId 用户ID
     * @return true-在选房时间内，false-不在选房时间内
     */
    @PostMapping("/user/judge")
    public R userJudgeChooseHouseTime(@RequestAttribute("CURRENT_USER_ID") String userId) {
        return chooseHouseService.judgeChooseHouseTime(userId);
    }

    /**
     * 判断当前企业用户是否在选房时间内
     * @param userId 企业用户ID
     * @return true-在选房时间内，false-不在选房时间内
     */
    @PostMapping("/enterprise/judge")
    public R enterpriseJudgeChooseHouseTime(@RequestAttribute("CURRENT_USER_ID") String userId) {
        return chooseHouseService.enterpriseJudgeChooseHouseTime(userId);
    }
}
