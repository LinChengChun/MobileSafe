package com.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.bean.BlackNumberInfo;
import com.mobilesafe.utils.LogUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/8/24.
 */
public class CallSmsSafeAdapter extends AppBaseAdapter<BlackNumberInfo>{

    private List<BlackNumberInfo> mListData; // 用于保存数据的集合
    private Context mContext; // 用于保存上下文
    private onClickListener onClickListener; // 自定义一个监听器

    public CallSmsSafeAdapter(Context context, List<BlackNumberInfo> list) {
        super(context, list);
        this.mListData = list;
        this.mContext = context;
    }

    public void setOnClickListener(onClickListener listener) {
        this.onClickListener = listener;
    }

    //    private enum ModeEnum{全部拦截, 电话拦截, 短信拦截}
    private String[] modeSafe = new String[]{"不拦截", "电话拦截", "短信拦截", "全部拦截"};
    /**
     * 有多少个条目，这个方法就会被调用多少次
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getItemView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // 定义一个view 容器
        // 优化1：减少创建子view的次数，如果有缓存，则复用缓存子view
        if (null == convertView){ // 假如第一次显示，则去创建子view
            convertView = View.inflate(mContext, R.layout.list_item_callsms, null); // 根据布局文件生成一个view
            // 优化2：根据id找到子view对象在内存中的地址并保存下来，下次使用直接使用地址
            viewHolder = new ViewHolder();
            viewHolder.tvBlackNumber = (TextView) convertView.findViewById(R.id.tv_black_number);
            viewHolder.tvBlackMode = (TextView) convertView.findViewById(R.id.tv_black_mode);
            viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(viewHolder); // 将当前容器与当前view绑定(设置标签)
        }else { // 如果view不为空，则表示该view可以复用
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BlackNumberInfo info = mListData.get(position);
        viewHolder.tvBlackNumber.setText(info.getNumber());
        viewHolder.tvBlackMode.setText(modeSafe[Integer.valueOf(info.getMode())]);

        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("删除"+position);
                if (onClickListener!=null)
                    onClickListener.onClick(position);
            }
        });
        return convertView;
    }

    /**
     * 定义一个删除按钮点击回调方法
     */
    public interface onClickListener{
        public void onClick(int position);
    }

    /**
     * 内容数据发生变化，应该刷新列表
     * @param list
     */
    public void notifyDataChange(List<BlackNumberInfo> list){
        super.notifyDataChange(list);
        super.notifyDataSetChanged();
    }

    /**
     * view 容器，用于根据id记录孩子
     */
    static class ViewHolder{
        TextView tvBlackNumber;
        TextView tvBlackMode;
        ImageView ivDelete;
    }
}
