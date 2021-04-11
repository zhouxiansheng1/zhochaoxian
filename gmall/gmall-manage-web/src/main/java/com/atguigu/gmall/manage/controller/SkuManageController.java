package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.service.ItemService;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
public class SkuManageController {

    @Reference
    ManageService manageService;
    @Reference
    ItemService itemService;
    @Reference
    ListService listService;

    @ResponseBody
    @RequestMapping("saveSku")
    public String saveSku(SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return "success";
    }

    @RequestMapping(value="skuInfoListBySpu")
    @ResponseBody
    public List<SkuInfo> getSkuInfoListBySpu(HttpServletRequest httpServletRequest){
        String spuId = httpServletRequest.getParameter("spuId");
        List<SkuInfo> skuInfoList = manageService.getSkuInfoListBySpu(spuId);
        return skuInfoList;
    }
    @RequestMapping("spuImageList")
    @ResponseBody
    public List<SpuImage> spuImageList(String spuId){
        return   manageService.getSpuImageBySpuId(spuId);
    }

    @RequestMapping(value = "onSale",method = RequestMethod.GET)
    @ResponseBody
    public void onSale(String skuId){
        SkuInfo skuInfo = itemService.getSkuInfo(skuId);
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        // 属性拷贝
        try {
            BeanUtils.copyProperties(skuLsInfo,skuInfo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        listService.saveSkuInfo(skuLsInfo);
    }
}
