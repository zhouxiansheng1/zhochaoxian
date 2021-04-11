package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {
    @Reference
    ListService listService;
    @Reference
    ManageService manageService;

    @RequestMapping("list.html")
    //@ResponseBody
    public String getList(SkuLsParams skuLsParams, HttpServletRequest request){
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        //获取属性值列表
        request.setAttribute("skuLsInfoList",skuLsResult.getSkuLsInfoList());
        //从属性值列表中获取平台属性值列表
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> attrList = manageService.getAttrList(attrValueIdList);
        request.setAttribute("attrList",attrList);
        //已选定的属性值列表
        List<BaseAttrValue> baseAttrValuesList = new ArrayList<>();
        String urlParam = makeUrlParam(skuLsParams);
        for (Iterator<BaseAttrInfo> iterator = attrList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo =  iterator.next();
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setUrlParam(urlParam);
                if (skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
                    for (String valueId : skuLsParams.getValueId()){
                        //选中的属性值和查询结果的属性值
                        if (valueId.equals(baseAttrValue.getId())){
                            iterator.remove();
                            //构造面包屑列表
                            BaseAttrValue baseAttrValueSelected = new BaseAttrValue();
                            baseAttrValueSelected.setValueName(baseAttrInfo.getAttrName()+":"+baseAttrValue.getValueName());
                            //去重
                            String makeUrlParam = makeUrlParam(skuLsParams, valueId);
                            baseAttrValueSelected.setUrlParam(makeUrlParam);
                            baseAttrValuesList.add(baseAttrValueSelected);
                        }
                    }
                }
            }
        }
        skuLsParams.setPageSize(2);

        int totalPages = (int) ((skuLsResult.getTotal() + skuLsParams.getPageSize()-1)/skuLsParams.getPageSize());
// skuLsResult.setTotalPages(totalPages);
        request.setAttribute("totalPages",totalPages);
        request.setAttribute("pageNo",skuLsParams.getPageNo());
        request.setAttribute("urlParam",urlParam);
        request.setAttribute("baseAttrValuesList",baseAttrValuesList);
        request.setAttribute("keyword",skuLsParams.getKeyword());
        //return JSON.toJSONString(search);
        return "list";
    }
    public String makeUrlParam(SkuLsParams skuLsParams,String... exculdeValueIds){
        String urlParam = "";
        if (skuLsParams.getKeyword()!=null){
            urlParam+="keyword="+skuLsParams.getKeyword();
        }
        if (skuLsParams.getCatalog3Id()!=null){
            if (urlParam.length()>0){
                urlParam+="&";
            }
            urlParam+="catalog3Id="+skuLsParams.getCatalog3Id();
        }
        //构造属性参数
        if (skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                String valueId = skuLsParams.getValueId()[i];
                if (exculdeValueIds.length>0&&exculdeValueIds!=null){
                    String exculdeValueId = exculdeValueIds[0];
                    if(exculdeValueId.equals(valueId)){
                        //跳出代码。后面代码不执行
                        continue;
                    }

                }
                if (urlParam.length()>0){
                    urlParam+="&";
                }
                urlParam+="valueId="+valueId;
            }
        }
        return urlParam;
    }

}
