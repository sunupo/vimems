package com.vimems.bean;

import java.util.Date;

public class Coach {
    private int coachID;
    private int adminID;
    private int imageID;
    private String loginName;
    private String loginPWD;
    private String gender;
    private Date birthdate;

    public Coach(int coachID, int adminID, int imageID, String loginName, String loginPWD, String gender, Date birthdate) {
        this.coachID = coachID;
        this.adminID = adminID;
        this.imageID = imageID;
        this.loginName = loginName;
        this.loginPWD = loginPWD;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public int getCoachID() {
        return coachID;
    }

    public int getAdminId() {
        return adminID;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getLoginPWD() {
        return loginPWD;
    }

    public String getGender() {
        return gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setCoachID(int coachID) {
        this.coachID = coachID;
    }

    public void setAdminId(int adminID) {
        this.adminID = adminID;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setLoginPWD(String loginPWD) {
        this.loginPWD = loginPWD;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "coachID=" + coachID +
                ", adminID=" + adminID +
                ", imageID=" + imageID +
                ", loginName='" + loginName + '\'' +
                ", loginPWD='" + loginPWD + '\'' +
                ", gender='" + gender + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }
}
