package com.example.iotp.Info;

import java.io.Serializable;

public class memberinfo implements Serializable {
    private String name;
    private String Local;
    private String lockerID;
    private String date;
    private String phoneNumber;
    private boolean access;

    public memberinfo(){
    }

    public memberinfo(memberinfo memberinfo){
        this.name = memberinfo.getName();
        this.Local = memberinfo.getLocal();
        this.lockerID = memberinfo.getLockerID();
        this.date = memberinfo.getDate();
        this.phoneNumber = memberinfo.getPhoneNumber();
        this.access = memberinfo.getAccess();
    }

    public memberinfo(String name,String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public memberinfo(String name, String Local, String lockerID, String date,String phoneNumber, boolean access){
        this.name = name;
        this.Local = Local;
        this.lockerID = lockerID;
        this.date = date;
        this.phoneNumber = phoneNumber;
        this.access = access;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getLockerID(){
        return this.lockerID;
    }
    public void setLockerID(String lockerID){
        this.lockerID = lockerID;
    }
    public String getDate(){
        return this.date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Boolean getAccess(){
        return this.access;
    }
    public void setAccess(boolean access){
        this.access = access;
    }
    public String getLocal() {
        return Local;
    }

    public void setLocal(String local) {
        Local = local;
    }
}
