package com.vimems.AdapterItem;

public class MemberItem {
    private String name;

    private int imageId;

    public MemberItem(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
