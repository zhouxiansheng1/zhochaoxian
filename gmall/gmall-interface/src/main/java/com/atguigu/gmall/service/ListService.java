package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

public interface ListService {
    //保存es的方法
    public void saveSkuInfo(SkuLsInfo skuLsInfo);
    //利用search查询检索的结果集
    public SkuLsResult search(SkuLsParams skuLsParams);
    //利用redis对排序调优，稀释es并发写操作（评分排序）
    public void incrHotScore(String skuId);
}
