package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {
    //获取三级分列表
    List<BaseCatalog1> getCatalog1();

    List<BaseCatalog2> getCatalog2(String catalog1Id);

    List<BaseCatalog3> getCatalog3(String catalog2Id);
    //根据三级id获取属性列表
    //根据三级id获取平台属性列表（对应的spu的平台属性列表）
    List<BaseAttrInfo> getAttrList(String catalog3Id);
    //保存属性到数据库
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
    //根据属性id获取属性对象
    BaseAttrInfo getAttrInfo(String attrId);
    //根据spu获取spu列表
    List<SpuInfo> getSpuInfoList(String catalog3Id);
    // 查询基本销售属性表
    List<BaseSaleAttr> getBaseSaleAttrList();
    //保存商品属性到数据库
    void saveSpuInfo(SpuInfo spuInfo);
    //保存一个sku
    void saveSkuInfo(SkuInfo skuInfo);
    //显示图片
    List<SpuImage> getSpuImageBySpuId(String spuId);

    List<SkuInfo> getSkuInfoListBySpu(String spuId);
    //根据spuId获取销售属性
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);
    //根据属性值id列表查询属性
    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
