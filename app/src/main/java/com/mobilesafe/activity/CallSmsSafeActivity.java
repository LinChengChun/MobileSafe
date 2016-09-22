package com.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobilesafe.R;
import com.mobilesafe.adapter.CallSmsSafeAdapter;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.BlackNumberInfo;
import com.mobilesafe.db.dao.BlackNumberDao;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/23.
 */
public class CallSmsSafeActivity extends BaseActivity implements CallSmsSafeAdapter.onClickListener{

    @BindView(R.id.lv_callsms_safe)
    ListView lvCallSmsSafe;

    @BindView(R.id.ll_loading)
    LinearLayout llLoading; // 正在加载线性布局控件

    private List<BlackNumberInfo> infos; // a list for save all the record
    private CallSmsSafeAdapter adapter; // 定义一个适配器

    // 定义添加黑名单号码对话框相关控件
    private EditText etBlackNumber;
    private CheckBox cbPhone;
    private CheckBox cbSms;
    private Button btnOk;
    private Button btnCancel;
    private AlertDialog dialog;

    private int offset = 0;
    private int maxNumber = 20;
    private int lengthBlackNumberOnDatabase = 0; // 数据库中黑名单记录条数
    private boolean isLoadingFinish = false; // 是否加载完数据库标志
    private BlackNumberDao mBlackNumberDao;

    @Override
    protected int initLayout() {
        return R.layout.activity_call_sms_safe;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initListener() {

        // 为Listview注册一个滚动事件的监听器
        lvCallSmsSafe.setOnScrollListener(new AbsListView.OnScrollListener() {
            // ListView滚动状态发生变化
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_IDLE: // 闲置状态，静止状态
                        LogUtil.d("静止状态");
                        int firstPosition = view.getFirstVisiblePosition();
                        int lastPosition = view.getLastVisiblePosition();
                        LogUtil.d("firstPosition = "+firstPosition+";lastPosition = "+lastPosition+";");
                        if (lastPosition == infos.size()-1){
                            LogUtil.d("列表滑动到最后一个位置，加载更多数据...");
                            loadingData(); // loading more Data
                        }
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL: // 手指触摸滚动状态
                        LogUtil.d("手指触摸滚动状态");
                        break;
                    case SCROLL_STATE_FLING: // 惯性滚动状态
                        LogUtil.d("惯性滚动状态");
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void initData() {
        mBlackNumberDao = BlackNumberDao.getIntance(getApplicationContext()); // 实例化业务操作类
        loadingData(); // loading more Data
    }

    /**
     * 从数据库加载记录到ListView
     */
    private void loadingData(){
        llLoading.setVisibility(View.VISIBLE); // 显示加载进度条
        if (isLoadingFinish){ // 如果已经加载完毕，则不再加载
            llLoading.setVisibility(View.INVISIBLE); // 数据加载完毕应该隐藏进度条
            PromptManager.showShortToast(CallSmsSafeActivity.this, "已经查询全部记录完毕！！");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
//                infos = mBlackNumberDao.findAll(); // 查询所有黑名单记录数据库
                if (infos == null) { // 如果集合为空，则添加到集合头部
                    infos = mBlackNumberDao.findPart(offset, maxNumber); // 加载部分数据
                }else {
                    List<BlackNumberInfo> subInfos = mBlackNumberDao.findPart(offset, maxNumber);
                    if (subInfos.isEmpty()){ // 查询数据库记录为0个，则认为已经全部加载完毕
                        isLoadingFinish = true;
                    }else { // 如果查询数据库不为空，则执行添加到集合流程
                        infos.addAll(subInfos); // 添加到集合尾部
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llLoading.setVisibility(View.INVISIBLE); // 数据加载完毕应该隐藏进度条
                        if (isLoadingFinish){ // 如果已经加载完毕，则不再加载
                            PromptManager.showShortToast(CallSmsSafeActivity.this, "已经查询全部记录完毕！！");
                            return;
                        }
                        if (adapter == null) {
                            adapter = new CallSmsSafeAdapter(CallSmsSafeActivity.this, infos); //实例化适配器
                            lvCallSmsSafe.setAdapter(adapter); // 为Listview设置适配器
                            adapter.setOnClickListener(CallSmsSafeActivity.this); // 因为实现了对应接口，所以把该Activity设置为点击删除按钮回调接口
                        }else {
                            adapter.notifyDataChange(infos); // 已经创建过适配器，则刷新数据
                        }
                        offset += maxNumber; // 加载记录的起始位置
                    }
                });
            }
        }).start();
    }

    /**
     * 添加黑名单对话框，监听button点击事件
     * @param view
     */
    public void addBlackNumber(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
        dialog = builder.create();
        View contentView = View.inflate(CallSmsSafeActivity.this, R.layout.dialog_add_blacknumber, null);

        etBlackNumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
        cbPhone = (CheckBox) contentView.findViewById(R.id.cb_phone);
        cbSms = (CheckBox) contentView.findViewById(R.id.cb_sms);
        btnOk = (Button) contentView.findViewById(R.id.ok);
        btnCancel = (Button) contentView.findViewById(R.id.cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击确定，应该保存当前号码和拦截模式
                String number = etBlackNumber.getText().toString().trim();// 获取输入的电话号码
                if (TextUtils.isEmpty(number)){
                    PromptManager.showShortToast(CallSmsSafeActivity.this, "黑名单号码不能为空");
                    return;
                }
                String mode = getCheckBoxMode(cbPhone, cbSms); // 根据CheckBox状态，获取拦截模式
                if (mode.equals("0")){
                    PromptManager.showShortToast(CallSmsSafeActivity.this, "请选择拦截模式");
                    return;
                }

                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(number);
                info.setMode(mode);

                if (mBlackNumberDao.find(number)){ // 假如已经保存过的，则更新拦截模式；否则添加一个新的对象
                    mBlackNumberDao.update(number, mode);
                } else {
                    mBlackNumberDao.add(number, mode); // 把该黑名单电话添加到数据库中
                }
                // infos.add(info); // 把对象添加到集合末尾
                infos.add(0, info); // 把对象添加到集合头部
                adapter.notifyDataChange(infos); // 适配器通知ListView数据已经发生改变

                dialog.dismiss(); // 关闭对话框
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击取消，应该关闭该对话框
                dialog.dismiss();
            }
        });

        dialog.setView(contentView,0,0,0,0);// 解决低版本 对话框无法覆盖满页面问题
        dialog.show();
    }

    /**
     * 获取用户设置的拦截模式
     * @param one
     * @param two
     * @return
     */
    private String getCheckBoxMode(CheckBox one, CheckBox two){
        if (one.isChecked() && two.isChecked())
            return "3";
        else if (one.isChecked())
            return "1";
        else if (two.isChecked() )
            return "2";
        else
            return "0";
    }

    /**
     * 从黑名单中删除电话号码回调接口
     * @param postion
     */
    @Override
    public void onClick(final int postion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 // 确认删除该黑名单电话号码
                mBlackNumberDao.delete(infos.get(postion).getNumber()); // 删除数据库
                infos.remove(postion); // 从ListView 中删除
                adapter.notifyDataChange(infos);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // 关闭对话框
            }
        });
        builder.setTitle("你真的要将该号码从黑名单中删除么？");
        builder.create().show(); // 创建对话框并显示
    }
}
