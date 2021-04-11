package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.passport.config.JwtUtil;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Value("${token.key}")
    String loginKey;

    @Reference
    private UserInfoService userInfoService;
    
    //主界面
    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl" ,originUrl);
        return "index";
    }

    //登录开发
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request,UserInfo userInfo){
        // 取得ip地址
        String remoteAddr  = request.getHeader("X-forwarded-for");

        if(userInfo!=null){
            UserInfo loginUser = userInfoService.login(userInfo);
            if (loginUser == null){
                return "fail";
            }else {
                //生成token
                Map map = new HashMap();
                map.put("userId",loginUser.getId());
                map.put("NickName",loginUser.getNickName());
                String token = JwtUtil.encode(loginKey, map, remoteAddr);
                return token;
            }
        }else {
            return "fail";
        }
    }

    //单点登录，多处验证
    @ResponseBody
    @RequestMapping("verify")
    public String verify(HttpServletRequest request){
        String token = request.getParameter("token");
        String currentIp = request.getParameter("currentIp");
        //检查token
        Map<String, Object> map = JwtUtil.decode(token, loginKey, currentIp);
        if (map!=null){
            String userId =(String)map.get("userId");
            UserInfo userInfo = userInfoService.verify(userId);
            if (userInfo!=null){
                return "success";
            }

        }
        return "fail";
    }



}

