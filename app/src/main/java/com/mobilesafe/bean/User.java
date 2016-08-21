package com.mobilesafe.bean;

import java.io.Serializable;

/**
 * 定义一个联系人类，用于保存某个联系人的信息
 * Created by Administrator on 2016/7/8.
 */
public class User implements Serializable{
    private String name;// 联系人名字
    private String number;// 联系人电话

    public User() {
    }

    public User(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
