package com.example.iotp.Info;

import java.io.Serializable;

public class MemoInfo implements Serializable {
    private String goodsName;
    private String txt;
    private String createDate;


    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String name) {
        this.goodsName = name;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }



}
