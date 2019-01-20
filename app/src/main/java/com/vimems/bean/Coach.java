package com.vimems.bean;

import java.util.Date;

public class Coach {
    private int coachID;
    private int adminID;
    private int imageID;
    //TODO
    private String coachName;
    private String coachLoginName;
    private String loginPassword;
    private String gender;
    private Date birthdate;
    private  String coachRank;

    public Coach(int coachID, int adminID, int imageID, String coachName, String coachLoginName, String loginPassword, String gender, Date birthdate, String coachRank) {
        this.coachID = coachID;
        this.adminID = adminID;
        this.imageID = imageID;
        this.coachName = coachName;
        this.coachLoginName = coachLoginName;
        this.loginPassword = loginPassword;
        this.gender = gender;
        this.birthdate = birthdate;
        this.coachRank = coachRank;
    }


    public int getCoachID() {
        return coachID;
    }

    public int getAdminId() {
        return adminID;
    }

    public String getcoachLoginName() {
        return coachLoginName;
    }

    public String getLoginPWD() {
        return loginPassword;
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

    public void setcoachLoginName(String coachLoginName) {
        this.coachLoginName = coachLoginName;
    }

    public void setLoginPWD(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
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
