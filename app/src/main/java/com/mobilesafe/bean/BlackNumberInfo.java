package com.mobilesafe.bean;

/**
 * 一个抽象类用于保存黑名单电话号码和拦截模式对象
 * Created by Administrator on 2016/8/24.
 */
public class BlackNumberInfo {

    private String number;
    private String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("number = ").append(this.number)
                .append(";mode = ").append(this.mode).append(";");
        return builder.toString();
    }
}
