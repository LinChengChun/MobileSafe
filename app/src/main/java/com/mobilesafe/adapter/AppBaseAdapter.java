package com.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Trim on 2016/4/24.
 * 抽象类不能被实例化，只能由其子类实例化
 */
public abstract class AppBaseAdapter<T> extends BaseAdapter {

    private List<T> mList = null;//定义 泛型list 列表，类似于数组
    private Context mContext = null;//当前上下文
    public LayoutInflater inflater;//定义 一个布局容器

    public AppBaseAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList != null && mList.size() > 0 ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent);

    public void notifyDataChange(List<T> list){
        this.mList = list;
    }
}
