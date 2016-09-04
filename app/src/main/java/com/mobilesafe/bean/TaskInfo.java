package com.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 进程信息业务类
 * Created by cclin on 2016/9/4.
 */
public class TaskInfo {
    private Drawable icon;
    private String name;
    private String packname;
    private long memorySize; // 所占内存空间
    private boolean isUserTask; // true 用户进程，false 系统进程
    private boolean isChecked; // 是否被选择

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackname() {
        return packname;
    }

    public void setPackname(String packname) {
        this.packname = packname;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserTask() {
        return isUserTask;
    }

    public void setUserTask(boolean userTask) {
        isUserTask = userTask;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packname='" + packname + '\'' +
                ", memorySize=" + memorySize +
                ", isUserTask=" + isUserTask +
                '}';
    }
}
