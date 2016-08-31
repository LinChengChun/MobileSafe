package com.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.bean.AppInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class AppInfosAdapter extends AppBaseAdapter<AppInfo>{

    private Context mContext;
    private List<AppInfo> mAppInfos;

    /**
     * 构造器
     * @param context
     * @param list
     */
    public AppInfosAdapter(Context context, List<AppInfo> list) {
        super(context, list);
        this.mContext = context;
        this.mAppInfos = list;
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_appinfo_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            viewHolder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
            viewHolder.tvAppLocation = (TextView) convertView.findViewById(R.id.tv_app_location);
            convertView.setTag(viewHolder); // 设置一个标签给convertView
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AppInfo appInfo = mAppInfos.get(position); // 获取当前app信息
        viewHolder.ivAppIcon.setImageDrawable(appInfo.getIcon()); // 设置小图标
        viewHolder.tvAppName.setText(appInfo.getName());
        if (appInfo.isRom()){
            viewHolder.tvAppLocation.setText("手机内存");
        }else {
            viewHolder.tvAppLocation.setText("SD卡");
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
        super.notifyDataChange(list);
        super.notifyDataSetChanged();
    }
}
