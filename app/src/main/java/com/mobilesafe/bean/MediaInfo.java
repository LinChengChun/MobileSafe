package com.mobilesafe.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/7/23.
 */
public class MediaInfo implements Parcelable {

    private String type; // 文件类型
    private String name; // 文件名称
    private long resID; // 文件id
    private Uri uri; // 文件uri
    private String path; // 文件在本地中的路径

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getResID() {
        return resID;
    }

    public void setResID(long resID) {
        this.resID = resID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeLong(this.resID);
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.path);
    }

    public MediaInfo() {
    }

    protected MediaInfo(Parcel in) {
        this.type = in.readString();
        this.name = in.readString();
        this.resID = in.readLong();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.path = in.readString();
    }

    public static final Parcelable.Creator<MediaInfo> CREATOR = new Parcelable.Creator<MediaInfo>() {
        @Override
        public MediaInfo createFromParcel(Parcel source) {
            return new MediaInfo(source);
        }

        @Override
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };
}
