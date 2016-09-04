package com.mobilesafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.bean.AppInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class AppInfosAdapter extends BaseAdapter {

    private Context mContext;
    private List<AppInfo> mAppInfos;
    private List<AppInfo> mUserAppInfos; // 用户应用集合
    private List<AppInfo> mSystemAppInfos; // 系统应用集合

    /**
     * 构造器
     * @param context
     * @param list
     */
    public AppInfosAdapter(Context context, List<AppInfo> list,
                           List<AppInfo> userAppInfos, List<AppInfo> systemAppInfos) {
//        super(context, list);
        this.mContext = context;
        this.mAppInfos = list;
        this.mUserAppInfos = userAppInfos;
        this.mSystemAppInfos = systemAppInfos;
    }

    @Override
    public int getCount() {
        return mUserAppInfos.size()+mSystemAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfo appInfo; // = mAppInfos.get(position); // 获取当前app信息
        if (position == 0){
            TextView tv = new TextView(mContext);
            tv.setTextSize(16.0f);
            tv.setTextColor(Color.WHITE);
            tv.setText("用户程序 "+mUserAppInfos.size()+"个");
            tv.setBackgroundColor(Color.GRAY);
            return tv;
        }else if (position == mUserAppInfos.size()+1){
            TextView tv = new TextView(mContext);
            tv.setTextSize(16.0f);
            tv.setTextColor(Color.WHITE);
            tv.setText("系统程序 "+mSystemAppInfos.size()+"个");
            tv.setBackgroundColor(Color.GRAY);
            return tv;
        }else if (position<=mUserAppInfos.size()){ // 用户应用程序
            int newposition = position-1;
            appInfo = mUserAppInfos.get(newposition);
        }else { // 系统应用程序
            int newposition = position-1-mUserAppInfos.size()-1;
            appInfo = mSystemAppInfos.get(newposition);
        }

        ViewHolder viewHolder = null;

        if (convertView != null && convertView instanceof RelativeLayout){ // 假如复用对象不为空，且类型是相对布局则可复用，否则从新实例化
            viewHolder = (ViewHolder) convertView.getTag();
        }else {
            convertView = View.inflate(mContext, R.layout.item_appinfo_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            viewHolder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
            viewHolder.tvAppLocation = (TextView) convertView.findViewById(R.id.tv_app_location);
            convertView.setTag(viewHolder); // 设置一个标签给convertView
        }

//        if (position<mUserAppInfos.size()){ // 当前位置小于用户应用长度，则显示用户应用
//            appInfo = mUserAppInfos.get(position);
//        }else { // 这些位置留给系统应用
//            int newposition = position - mUserAppInfos.size();
//            appInfo = mSystemAppInfos.get(newposition);
//        }

        viewHolder.ivAppIcon.setImageDrawable(appInfo.getIcon()); // 设置小图标
        viewHolder.tvAppName.setText(appInfo.getName());
        if (appInfo.isRom()){ // 判断是否安装在手机内存
            viewHolder.tvAppLocation.setText("手机内存");
        }else {
            viewHolder.tvAppLocation.setText("外部存储");
        }

        return convertView;
    }

    static class ViewHolder{
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppLocation;
    }

    /**
     * 内容数据发生变化，应该刷新列表
     * @param list
     */
    public void notifyDataChange(List<AppInfo> list){
//        super.notifyDataChange(list);
        super.notifyDataSetChanged();
    }

    public void notifyDataChange(List<AppInfo> list, List<AppInfo> userAppInfos, List<AppInfo> systemAppInfos){
//        super.notifyDataChange(list);
        this.mAppInfos = list;
        this.mUserAppInfos = userAppInfos;
        this.mSystemAppInfos = systemAppInfos;
        super.notifyDataSetChanged();
    }
}
