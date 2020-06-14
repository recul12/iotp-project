package com.example.iotp.Info;

import java.io.Serializable;

public class logInfo implements Serializable {
    private String logTime;
    private String contentsRFID;
    private boolean inOUT;

    public logInfo(){
    }

    public logInfo(String logTime, String contentsRFID, boolean inOUT){
        this.logTime = logTime;
        this.contentsRFID = contentsRFID;
        this.inOUT = inOUT;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getContentsRFID() {
        return contentsRFID;
    }

    public void setContentsRFID(String contentsRFID) {
        this.contentsRFID = contentsRFID;
    }

    public boolean getInOUT() {
        return inOUT;
    }

    public void setInOUT(boolean inOUT) {
        this.inOUT = inOUT;
    }

}
