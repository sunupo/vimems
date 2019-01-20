package com.vimems.AdapterItem;

public class CoachItem {
    private String name;

    private int imageId;

    private int coachID;

    public int getCoachID() {
        return coachID;
    }

    public void setCoachID(int coachID) {
        this.coachID = coachID;
    }

    public CoachItem(String name, int imageId, int coachID) {
        this.name = name;
        this.imageId = imageId;
        this.coachID = coachID;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
