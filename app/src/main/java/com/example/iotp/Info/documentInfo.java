package com.example.iotp.Info;

import java.io.Serializable;

public class documentInfo implements Serializable {
    private String goodsName;
    private String detail;
    private boolean inOut;
    private String photoURL;

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public documentInfo(){
    }

    public documentInfo(String goodsName, String detail, boolean inOut, String photoURL){
        this.goodsName = goodsName;
        this.detail = detail;
        this.inOut = inOut;
        this.photoURL = photoURL;
    }

    public String getGoodsName(){
        return this.goodsName;
    }
    public void setGoodsName(){
        this.goodsName = goodsName;
    }
    public String getDetail(){
        return this.detail;
    }
    public void setDetail(){
        this.detail = detail;
    }
    public Boolean getInOut(){
        return this.inOut;
    }
    public void setInOut(){
        this.inOut = inOut;
    }
}
