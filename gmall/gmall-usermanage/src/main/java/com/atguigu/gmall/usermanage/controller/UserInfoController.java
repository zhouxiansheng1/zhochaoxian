package com.atguigu.gmall.usermanage.controller;

import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    // @ResponseBody 有两个作用：一个返回json 字符串 将方法中的数据直接输入到页面
    @RequestMapping("findAll")
    @ResponseBody
    public List<UserInfo> findAll(){
        return   userInfoService.getUserInfoListAll();
    }


}










