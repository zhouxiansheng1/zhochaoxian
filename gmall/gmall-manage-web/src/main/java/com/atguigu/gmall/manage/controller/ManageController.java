package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class ManageController {

    @Reference
    ManageService manageService;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("attrListPage")
    public String getAttrListPage() {
        return "attrListPage";
    }

    @RequestMapping("spuListPage")
    public String getSpuListPage() {
        return "spuListPage";
    }

    @ResponseBody
    @RequestMapping("spuInfoList")
    public List<SpuInfo> getSpuInfoList(String catalog3Id) {
        return manageService.getSpuInfoList(catalog3Id);
    }

    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> getBaseSaleAttrList(){
        return   manageService.getBaseSaleAttrList();
    }


    @ResponseBody
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getCatalog1();
    }

    @ResponseBody
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        return manageService.getCatalog2(catalog1Id);
    }

    @ResponseBody
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        return manageService.getCatalog3(catalog2Id);
    }

    @ResponseBody
    @RequestMapping(value = "attrInfoList",method = RequestMethod.GET)
    public List<BaseAttrInfo> attrInfoList(String catalog3Id) {
        //return manageService.getAttrList(catalog3Id);
        List<BaseAttrInfo> attrList = manageService.getAttrList(catalog3Id);
        return attrList;

    }

    @ResponseBody
    @RequestMapping(value = "saveAttrInfo", method = RequestMethod.POST)
    public String saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo(baseAttrInfo);
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "getAttrValueList", method = RequestMethod.POST)
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        return manageService.getAttrInfo(attrId).getAttrValueList();
    }

    @ResponseBody
    @RequestMapping(value = "spuSaleAttrList",method = RequestMethod.GET)
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
        return manageService.getSpuSaleAttrList(spuId);
    }
}
