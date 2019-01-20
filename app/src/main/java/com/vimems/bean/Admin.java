package com.vimems.bean;

public class Admin {
    private int adminID;
    private String adminName;
    private String loginName;
    private String adminPassword;
    private String gender;

    public Admin(int adminID, String adminName, String loginName, String adminPassword, String gender) {
        this.adminID = adminID;
        this.adminName = adminName;
        this.loginName = loginName;
        this.adminPassword = adminPassword;
        this.gender = gender;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminID=" + adminID +
                ", adminName='" + adminName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
