package com.atguigu.gmall.bean;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class SpuSaleAttr implements Serializable{
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String spuId;

    @Column
    private String saleAttrId;

    @Column
    private String saleAttrName;

    public List<SpuSaleAttrValue> getSpuSaleAttrValueList() {
        return spuSaleAttrValueList;
    }

    public void setSpuSaleAttrValueList(List<SpuSaleAttrValue> spuSaleAttrValueList) {
        this.spuSaleAttrValueList = spuSaleAttrValueList;
    }

    @Transient
    private List<SpuSaleAttrValue> spuSaleAttrValueList;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public String getSaleAttrId() {
        return saleAttrId;
    }

    public void setSaleAttrId(String saleAttrId) {
        this.saleAttrId = saleAttrId;
    }

    public String getSaleAttrName() {
        return saleAttrName;
    }

    public void setSaleAttrName(String saleAttrName) {
        this.saleAttrName = saleAttrName;
    }
}
