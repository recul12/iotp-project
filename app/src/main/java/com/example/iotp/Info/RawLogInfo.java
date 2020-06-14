package com.example.iotp.Info;

public class RawLogInfo {
    private String ID;
    private String lockerID;
    private boolean inOut;

    public RawLogInfo(){

    }

    public RawLogInfo(RawLogInfo rawLogInfo){
        this.ID = rawLogInfo.getID();
        this.lockerID = rawLogInfo.getLockerID();
        this.inOut = rawLogInfo.isInOut();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLockerID() {
        return lockerID;
    }

    public void setLockerID(String lockerID) {
        this.lockerID = lockerID;
    }

    public boolean isInOut() {
        return inOut;
    }

    public void setInOut(boolean inOut) {
        this.inOut = inOut;
    }
}
