package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SpuManageController {
    @Reference
    private ManageService manageService;

    @ResponseBody
    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return "success";
    }
}
