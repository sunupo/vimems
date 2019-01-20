package com.vimems.bean;

import java.util.Date;

public class Member {
    private String memberName;
    private int memberID;
    private int coachID;
    private String gender;
    private Date birthdate;
    private double height;
    private double weight;
    private Date date;
    private int age;

    public Member(String memberName, int memberID, int coachID, String gender, Date birthdate, double height, double weight, Date date, int age) {
        this.memberName = memberName;
        this.memberID = memberID;
        this.coachID = coachID;
        this.gender = gender;
        this.birthdate = birthdate;
        this.height = height;
        this.weight = weight;
        this.date = date;
        this.age = age;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getCoachID() {
        return coachID;
    }

    public void setCoachID(int coachID) {
        this.coachID = coachID;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberID=" + memberID +
                ", coachID=" + coachID +
                ", gender='" + gender + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", date=" + date +
                ", age=" + age +
                '}';
    }
}
