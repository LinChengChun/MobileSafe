package com.mobilesafe.bean;

/**
 * Created by Administrator on 2016/7/4.
 */
public class MyItem {
    private String Name;
    private int drawableId;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public MyItem(String name, int drawableId) {
        this.Name = name;
        this.drawableId = drawableId;
    }
}
