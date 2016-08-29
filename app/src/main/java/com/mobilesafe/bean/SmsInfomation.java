package com.mobilesafe.bean;

/**
 * Created by Administrator on 2016/8/29.
 */
/**
 * 用于描述短信结构的类
 */
public class SmsInfomation{
    private String body;// 短信内容
    private String address;// 发送者
    private String type;// 短信类型
    private String date;// 时间

    public SmsInfomation() {
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("body: "+body).append("address: "+address).append("type: "+type).append("date: "+date).append(';');
        return builder.toString();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
