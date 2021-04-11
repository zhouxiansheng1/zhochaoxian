package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;

import java.util.List;

public interface ItemService {
    //查询sku详情
    SkuInfo getSkuInfo(String skuId);
    //查询商品销售属性
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo);
    //根据销售属性值查询商品
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

}
