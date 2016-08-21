package com.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.bean.User;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public class SelectContactListViewAdapter extends AppBaseAdapter<User>{

    private Context mContext = null;
    private List<User> mListUser = null; // 集合用于保存联系人简单数据

    public SelectContactListViewAdapter(Context context, List<User> list) {
        super(context, list);
        this.mContext = context;
        this.mListUser = list;
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user_layout, null, false);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name); // 检索id
            viewHolder.tvUserNumber = (TextView) convertView.findViewById(R.id.tv_user_number); // 检索id
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUserName.setText(mListUser.get(position).getName());
        viewHolder.tvUserNumber.setText(mListUser.get(position).getNumber());
        return convertView;
    }
    private class ViewHolder{
        TextView tvUserName;
        TextView tvUserNumber;
    }
}
