package com.mobilesafe.bean;

/**
 * 用于描述每个应用流量情况和信息
 * Created by Administrator on 2016/9/23.
 */

public class AppTrafficInfo {

    private String appName; // 应用名称
    private String uploadTraffic; // 上传流量大小
    private String downloadTraffic; // 下载流量大小

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUploadTraffic() {
        return uploadTraffic;
    }

    public void setUploadTraffic(String uploadTraffic) {
        this.uploadTraffic = uploadTraffic;
    }

    public String getDownloadTraffic() {
        return downloadTraffic;
    }

    public void setDownloadTraffic(String downloadTraffic) {
        this.downloadTraffic = downloadTraffic;
    }
}
