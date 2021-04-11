package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    // 根据三级分类id查询平台属性属性表
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(Long catalog3Id);

    List<BaseAttrInfo> selectAttrInfoList(long catalog3Id);

    //根据平台属性之ids查询
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String attrValueIds);
}
