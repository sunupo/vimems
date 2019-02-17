package com.vimems.bean;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class CustomMusclePara extends LitePalSupport implements Serializable {
    private int muscleID;//肌肉ID
    private int memberID;//会员ID
    private int trainingModeCode;//自定义训练1、vip训练3
    private int trainingModuleCode;//增肌1、减脂2、塑形3、康复4
    private int trainingModuleLevel;//初级1、中级2、高级3；放松1、镇痛2、损伤3
    private int lowFrequency;//低频0-150，在写characteristics的时候，应该用两个byte来传递值
    private int highFrequency;//高频2000-10000，在写characteristics的时候，应该用两个byte来传递值
    private int pulseWidth;//脉宽0-100%
    private int pulsePeriod;//脉冲时间1-30秒
    private int intermittentPeriod;//间歇时间0-30秒
    private int intensity;//强度0-100%

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getMuscleID() {
        return muscleID;
    }

    public void setMuscleID(int muscleID) {
        this.muscleID = muscleID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getTrainingModeCode() {
        return trainingModeCode;
    }

    public void setTrainingModeCode(int trainingModeCode) {
        this.trainingModeCode = trainingModeCode;
    }

    public int getTrainingModuleCode() {
        return trainingModuleCode;
    }

    public void setTrainingModuleCode(int trainingModuleCode) {
        this.trainingModuleCode = trainingModuleCode;
    }

    public int getTrainingModuleLevel() {
        return trainingModuleLevel;
    }

    public void setTrainingModuleLevel(int trainingModuleLevel) {
        this.trainingModuleLevel = trainingModuleLevel;
    }

    public int getLowFrequency() {
        return lowFrequency;
    }

    public void setLowFrequency(int lowFrequency) {
        this.lowFrequency = lowFrequency;
    }

    public int getHighFrequency() {
        return highFrequency;
    }

    public void setHighFrequency(int highFrequency) {
        this.highFrequency = highFrequency;
    }

    public int getPulseWidth() {
        return pulseWidth;
    }

    public void setPulseWidth(int pulseWidth) {
        this.pulseWidth = pulseWidth;
    }

    public int getPulsePeriod() {
        return pulsePeriod;
    }

    public void setPulsePeriod(int pulsePeriod) {
        this.pulsePeriod = pulsePeriod;
    }

    public int getIntermittentPeriod() {
        return intermittentPeriod;
    }

    public void setIntermittentPeriod(int intermittentPeriod) {
        this.intermittentPeriod = intermittentPeriod;
    }
}
