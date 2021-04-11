package com.atguigu.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class SpuImage implements Serializable {

    @Id
    @Column
    private String id;

    @Column
    private String spuId;

    @Column
    private String imgUrl;

    @Column
    private String imgName;

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
