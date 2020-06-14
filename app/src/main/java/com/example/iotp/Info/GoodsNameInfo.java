package com.example.iotp.Info;

public class GoodsNameInfo {
    String RFID;
    String goodsName;

    public GoodsNameInfo(){

    }

    public GoodsNameInfo(String RFID, String goodsName){
        this.RFID = RFID;
        this.goodsName = goodsName;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
