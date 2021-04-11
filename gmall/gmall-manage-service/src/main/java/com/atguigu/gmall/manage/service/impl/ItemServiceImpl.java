package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.RedisUtil;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.constant.ManageConst;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService{
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    public RedisUtil redisUtil;

    @Override
    public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo = null;
        try {
            Jedis jedis = redisUtil.getJedis();
            // 定义key sku:skuId:info
            String skuInfoKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
            // 根据key 取数据
            String skuJson  = jedis.get(skuInfoKey);
            // 说明缓存中没有数据
            if ("".equals(skuJson) || skuJson==null){
                // 应该加锁，定义加锁的key sku:skuId:lock
                System.out.println("获取锁！");
                String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                // 返回set命令的执行结果
                String lockKey  = jedis.set(skuLockKey, "OK", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                if ("OK".equals(lockKey)){
                    System.out.println("准备从数据库中取得数据");
                    // 从数据中取得数据
                    skuInfo = getSkuInfoDB(skuId);
                    // 将对象转换为字符串
                    String jsonString  = JSON.toJSONString(skuInfo);
                    // 将数据放入redis
                    jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT,jsonString);
                    return skuInfo;
                }else {
                    // 睡一会
                    Thread.sleep(1000);
                    // 自旋
                    return getSkuInfo(skuId);
                }
            }else {
                // 直接取得redis中的数据
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                jedis.close();
                return skuInfo;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return getSkuInfoDB(skuId);
    }

    public SkuInfo getSkuInfoDB(String skuId){
        // 单纯的信息
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        // 查询图片
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> imageList = skuImageMapper.select(skuImage);
// 将查询出来所有图片赋予对象
        skuInfo.setSkuImageList(imageList);
        // 查询属性值
        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.select(skuSaleAttrValue);

        // 将查询出来所有商品属性值赋给对象
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueList);
        //平台属性封装到skuinfo中
        //select*from skuAttrValue where skuId=?
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValues);
        return skuInfo;
    }
   /* public SkuInfo getSkuInfo(String skuId){
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }
*/
    @Override
    public List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(Long.parseLong(skuInfo.getId()),Long.parseLong(skuInfo.getSpuId()));
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.SelectSkuSaleAttrValueListBySpu(spuId);
    }
}
