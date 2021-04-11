package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ItemService;
import com.atguigu.gmall.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    ItemService itemService;
    @Reference
    ListService listService;

    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable("skuId") String skuId, HttpServletRequest request) {
        //基本sku信息
        SkuInfo skuInfo = itemService.getSkuInfo(skuId);
        request.setAttribute("skuInfo", skuInfo);
        //sku的平台属性销售属性
        List<SpuSaleAttr> saleAttrList = itemService.selectSpuSaleAttrListCheckBySku(skuInfo);
        request.setAttribute("saleAttrList", saleAttrList);

        List<SkuSaleAttrValue> skuSaleAttrValueList = itemService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        //把列表变换成 valueid1|valueid2|valueid3 ：skuId  的 哈希表 用于在页面中定位查询
        String valueIdsKey ="";
        Map<String,String> valuesSkuMap=new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if(valueIdsKey.length()!=0){
                //拼接字符串
                valueIdsKey += "|";
            }
            valueIdsKey += skuSaleAttrValue.getSaleAttrValueId();
            // 什么时候将 valueIdsKey ，skuId 放入map 中 什么时候不拼接 skuId?
            // 不相等的时候，停止拼接 ，循环到最后的时候，停止拼接
            if ((i+1)==skuSaleAttrValueList.size()||!skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i+1).getSkuId())){
                valuesSkuMap.put(valueIdsKey,skuSaleAttrValue.getSkuId());
                valueIdsKey = "";
            }
        }
        String valuesSkuJson = JSON.toJSONString(valuesSkuMap);
        request.setAttribute("valuesSkuJson" ,valuesSkuJson);
        // 点击商品详情，记录商品的访问次数
        listService.incrHotScore(skuId);
        return "item";
    }


}
