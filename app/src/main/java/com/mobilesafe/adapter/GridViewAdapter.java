package com.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.bean.MyItem;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class GridViewAdapter extends AppBaseAdapter<MyItem>{

    private List<MyItem> mListData = null;
    private Context mContext = null;

    public GridViewAdapter(Context context, List<MyItem> list) {
        super(context, list);
        this.mListData = list;
        this.mContext = context;
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView==null){
            viewHolder = new ViewHolder();// new a viewHolder
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_item);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(mListData.get(position).getName());
        viewHolder.imageView.setImageResource(mListData.get(position).getDrawableId());

        return convertView;
    }

    class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}
