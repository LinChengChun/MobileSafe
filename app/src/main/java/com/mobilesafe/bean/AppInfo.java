package com.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 应用程序信息的业务Bean
 * Created by Administrator on 2016/8/31.
 */
public class AppInfo {

    private Drawable icon; // 应用图标
    private String name; // 应用名
    private String packname; // 包名
    private boolean isRom; // 是否安装在内存
    private boolean userApp; // 是否用户应用

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

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
