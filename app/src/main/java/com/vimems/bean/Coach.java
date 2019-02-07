package com.vimems.bean;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

public class Coach extends LitePalSupport {
    private int coachID;
    private int adminID;
    private int imageID;

    private String coachName;
    private String coachLoginName;
    private String loginPassword;
    private String gender;
    private Date birthdate;
    private  String coachRank;


    public int getCoachID() {
        return coachID;
    }

    public void setCoachID(int coachID) {
        this.coachID = coachID;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getCoachLoginName() {
        return coachLoginName;
    }

    public void setCoachLoginName(String coachLoginName) {
        this.coachLoginName = coachLoginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getCoachRank() {
        return coachRank;
    }

    public void setCoachRank(String coachRank) {
        this.coachRank = coachRank;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "coachID=" + coachID +
                ", adminID=" + adminID +
                ", imageID=" + imageID +
                ", coachName='" + coachName + '\'' +
                ", coachLoginName='" + coachLoginName + '\'' +
                ", loginPassword='" + loginPassword + '\'' +
                ", gender='" + gender + '\'' +
                ", birthdate=" + birthdate +
                ", coachRank='" + coachRank + '\'' +
                '}';
    }
}
