package com.mobilesafe.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.TaskInfo;
import com.mobilesafe.engine.TaskInfoProvider;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;
import com.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cclin on 2016/9/4.
 */
public class TaskManagerActivity extends BaseActivity{

    @BindView(R.id.tv_process_count)
    TextView tvProcessCount;

    @BindView(R.id.tv_mem_info)
    TextView tvMemInfo;

    @BindView(R.id.lv_task_manager)
    ListView lvTaskManager;

    @BindView(R.id.ll_task_loading)
    LinearLayout llTaskLoading;

    @BindView(R.id.tv_process_status)
    TextView tvProcessStatus;

    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;
    private TaskManagerAdapter adapter; // 适配器

    private long availMem;
    private long totalMem;
    private int processCount;

    private final int SHOW_USER_TASK_INFO = 0;
    private final int SHOW_SYSTEM_TASK_INFO = 1;
    private final int SHOW_ALL_TASK_INFO = 2;

    private AlertDialog dialog; // 用于弹出设置对话框
    private int taskInfoShowMode = SHOW_ALL_TASK_INFO;
    private List<TaskInfo> currentShowTaskInfo = null; // 用于存储当前列表显示的集合类型

    @Override
    protected int initLayout() {
        return R.layout.activity_task_manager;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {

        int processCount = SystemInfoUtils.getRunningProcessCount(this);
//        tvProcessCount.setText("运行中的进程:"+processCount+"个");
        LogUtil.d("processCount = "+processCount);

        fillData(); // 填充数据
    }

    /**
     * 用于填充数据
     */
    private void fillData() {
        llTaskLoading.setVisibility(View.VISIBLE); // 显示加载进度条
        tvProcessStatus.setVisibility(View.INVISIBLE); // 隐藏ListView上方进程类型个数的显示
        new Thread(new Runnable() {
            @Override
            public void run() {
                allTaskInfos = TaskInfoProvider.getServiceInfo(TaskManagerActivity.this); // 加载系统运行进程信息
                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo info: allTaskInfos){ // 应用信息分类
                    if (info.isUserTask()) // 如果是用户进程的话，添加到用户集合
                        userTaskInfos.add(info);
                    else systemTaskInfos.add(info);
                }
//                currentShowTaskInfo = allTaskInfos; // 初始化为全部进程类型显示

                // 更新设置界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llTaskLoading.setVisibility(View.INVISIBLE); // 关闭进度条显示
                        tvProcessStatus.setVisibility(View.VISIBLE); // 打开ListView上方进程类型个数的显示
                        setTitle(); // 设置状态标题

                        tvProcessStatus.setText("用户进程 "+userTaskInfos.size()+"个");
                        if (adapter == null) {
                            adapter = new TaskManagerAdapter();
                            lvTaskManager.setAdapter(adapter); // 为ListView设置适配器
                        }else {
                            adapter.notifyDataSetChanged(); // 如果已经设置过适配器，则只刷新ListView
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void initListener() {
        // 监听ListView滚动事件
        lvTaskManager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfos != null && systemTaskInfos != null){
//                    if (taskInfoShowMode == SHOW_ALL_TASK_INFO || taskInfoShowMode == SHOW_USER_TASK_INFO){
                        if (firstVisibleItem>userTaskInfos.size()){
                            tvProcessStatus.setText("系统进程: "+systemTaskInfos.size()+"个");
                        }else{
                            tvProcessStatus.setText("用户进程: "+userTaskInfos.size()+"个");
                        }
//                    }else {
//                        tvProcessStatus.setText("系统进程: "+systemTaskInfos.size()+"个");
//                    }
                }
            }
        });

        lvTaskManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo; // 声明一个缓存当前进程信息的对象
                if (position == 0){
                    return;
                }else if (position == userTaskInfos.size()+1){
                    return;
                }else if (position <= userTaskInfos.size()){ // 用户应用
                    int newposition = position-1;
                    taskInfo = userTaskInfos.get(newposition);
                }else { // 系统应用
                    int newposition = position-1-userTaskInfos.size()-1;
                    taskInfo = systemTaskInfos.get(newposition);
                }
                LogUtil.d("OnItemClick: "+taskInfo.getPackname());

                if (getPackageName().equals(taskInfo.getPackname())) // 如果是手机卫士本身，则过滤掉，不切换CheckBox状态
                    return;

                ViewHolder viewHolder = (ViewHolder) view.getTag(); // 获取当前View条目的标签
                if (taskInfo.isChecked()){ // 如果当前条目处于被选择状态，那么应该取消
                    taskInfo.setChecked(false);
                    viewHolder.cbSatus.setChecked(false); // 更新CheckBox 状态UI
                }else {
                    taskInfo.setChecked(true);
                    viewHolder.cbSatus.setChecked(true); // 更新CheckBox 状态UI
                }
            }
        });
    }

    /**
     * 更新内存状态标题栏UI
     */
    private void setTitle(){
        processCount = allTaskInfos.size(); // 初始化时，记录运行进程个数
        tvProcessCount.setText("运行中的进程:"+processCount+"个");
        availMem = SystemInfoUtils.getAvailMemory(TaskManagerActivity.this);
        totalMem = SystemInfoUtils.getTotalMemory(TaskManagerActivity.this);
        tvMemInfo.setText("剩余/总内存:"+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)+"/"+Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
    }

    private class TaskManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
//            int listCount = 0;
//            if (taskInfoShowMode == SHOW_ALL_TASK_INFO)
//                listCount = userTaskInfos.size()+systemTaskInfos.size()+1+1;
//            else if (taskInfoShowMode == SHOW_USER_TASK_INFO)
//                listCount = userTaskInfos.size()+1;
//            else if (taskInfoShowMode == SHOW_SYSTEM_TASK_INFO)
//                listCount = systemTaskInfos.size()+1;
//            return listCount;
            SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), MODE_PRIVATE);

            if (sp.getBoolean("showsystem", false)){
                return userTaskInfos.size()+1+systemTaskInfos.size()+1;
            }else {
                return userTaskInfos.size()+1;
            }
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
            TaskInfo taskInfo = null; // 声明一个缓存当前进程信息的对象
//            if (taskInfoShowMode == SHOW_ALL_TASK_INFO || taskInfoShowMode == SHOW_USER_TASK_INFO) {
                if (position == 0) {
                    TextView tv = new TextView(TaskManagerActivity.this);
                    tv.setTextSize(16.0f);
                    tv.setTextColor(Color.WHITE);
                    tv.setText("用户进程: " + userTaskInfos.size() + "个");
                    tv.setBackgroundColor(Color.GRAY);
                    return tv;
                } else if (position == (userTaskInfos.size() + 1)) {
                    TextView tv = new TextView(TaskManagerActivity.this);
                    tv.setTextSize(16.0f);
                    tv.setTextColor(Color.WHITE);
                    tv.setText("系统进程: " + systemTaskInfos.size() + "个");
                    tv.setBackgroundColor(Color.GRAY);
                    return tv;
                } else if (position <= userTaskInfos.size()) { // 用户应用程序
                    int newposition = position - 1;
                    taskInfo = userTaskInfos.get(newposition);
                } else { // 系统应用程序
                    int newposition = position - 1 - userTaskInfos.size() - 1;
                    taskInfo = systemTaskInfos.get(newposition);
                }
//            }else {
//                if (position == 0) {
//                    TextView tv = new TextView(TaskManagerActivity.this);
//                    tv.setTextSize(16.0f);
//                    tv.setTextColor(Color.WHITE);
//                    tv.setText("系统进程: " + systemTaskInfos.size() + "个");
//                    tv.setBackgroundColor(Color.GRAY);
//                    return tv;
//                }else if (position <= systemTaskInfos.size()) { // 用户应用程序
//                    int newposition = position - 1;
//                    taskInfo = systemTaskInfos.get(newposition);
//                }
//            }
            ViewHolder viewHolder;
            if (convertView!=null && convertView instanceof RelativeLayout){ // View对象可以被复用
                viewHolder = (ViewHolder) convertView.getTag();
                LogUtil.d("复用缓存。。。"+position);
            }else {
                viewHolder = new ViewHolder();
                convertView = View.inflate(TaskManagerActivity.this, R.layout.list_item_taskinfo, null);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_task_icon);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_task_name);
                viewHolder.tvSize = (TextView) convertView.findViewById(R.id.tv_task_size);
                viewHolder.cbSatus = (CheckBox) convertView.findViewById(R.id.cb_status);
                convertView.setTag(viewHolder);
                LogUtil.d("创建新的View对象。。。"+position);
            }

            viewHolder.ivIcon.setImageDrawable(taskInfo.getIcon()); // 设置图标
            viewHolder.tvName.setText(taskInfo.getName());
            viewHolder.tvSize.setText(Formatter.formatFileSize(TaskManagerActivity.this ,taskInfo.getMemorySize()));
            viewHolder.cbSatus.setChecked(taskInfo.isChecked()); // 从适配器中获取数据，获取选择状态

            if (getPackageName().equals(taskInfo.getPackname())){ // 假如是应用本身，则隐藏可选框框
                viewHolder.cbSatus.setVisibility(View.INVISIBLE); // 设置可见性
            }else {
                viewHolder.cbSatus.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    static class ViewHolder{

        ImageView ivIcon;
        TextView tvName;
        TextView tvSize;
        CheckBox cbSatus;

    }

    /**
     * 用于选择全部
     * @param view
     */
    public void selectAll(View view) {
//        for (TaskInfo info: currentShowTaskInfo){
        for (TaskInfo info: allTaskInfos){
            if (getPackageName().equals(info.getPackname()))
                continue;
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 用于反选
     * @param view
     */
    public void selectOppo(View view) {
//        for (TaskInfo info: currentShowTaskInfo){
        for (TaskInfo info: allTaskInfos){
            if (getPackageName().equals(info.getPackname()))
                continue;
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 一键清理
     * @param view
     */
    public void killAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE); // 获取系统活动管理服务
        int count= 0; // 用于计数杀掉了几个进程
        long sumSize = 0; // 用于计数释放了多少空间
        List<TaskInfo> killTaskInfos = new ArrayList<TaskInfo>(); // 保存被杀死进程的集合
        for (TaskInfo info: allTaskInfos){
            if (info.isChecked()){ // 该条目被勾选，杀死这个进程
                am.killBackgroundProcesses(info.getPackname()); // 杀掉后台进程
                if (info.isUserTask()){ // 判断是属于用户应用还是系统应用，从集合中移除
                    userTaskInfos.remove(info);
                }else {
                    systemTaskInfos.remove(info);
                }
//                allTaskInfos.remove(info) // 报错，集合迭代期间，不能改变其大小
                killTaskInfos.add(info);
                count++;
                sumSize += info.getMemorySize();
            }
        }
        allTaskInfos.removeAll(killTaskInfos); // 移除已被杀死的进程
        adapter.notifyDataSetChanged();
        PromptManager.showShortToast(TaskManagerActivity.this, "杀掉了"+count
                +"个进程，共释放了"+Formatter.formatFileSize(TaskManagerActivity.this, sumSize)+"的空间");
//        fillData(); // 重新加载填充列表数据

        processCount -= count; // 重新计算进程个数
        availMem += sumSize; // 重新计算剩余内存
        tvProcessCount.setText("运行中的进程:"+processCount+"个");
        tvMemInfo.setText("剩余/总内存:"+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)+"/"+Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
    }

    /**
     * 用于设置
     * @param view
     */
    public void setting(View view) {

        View contentView = View.inflate(TaskManagerActivity.this, R.layout.dialog_setting_taskmanager, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(TaskManagerActivity.this);
        dialog = builder.create(); // 显示对话框
        dialog.setView(contentView, 0, 0, 0, 0);

        final RadioGroup rgShowProcess = (RadioGroup) contentView.findViewById(R.id.rg_show_process);

        Button ok = (Button) contentView.findViewById(R.id.ok);
        Button cancel = (Button) contentView.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("id = "+rgShowProcess.getCheckedRadioButtonId());

                switch (rgShowProcess.getCheckedRadioButtonId()){
                    case R.id.rb_show_all_process:
                        LogUtil.d("显示所有类型进程");
                        taskInfoShowMode = SHOW_ALL_TASK_INFO;
                        currentShowTaskInfo = allTaskInfos;
                        break;
                    case R.id.rb_show_system_process:
                        LogUtil.d("显示系统应用进程");
                        taskInfoShowMode = SHOW_SYSTEM_TASK_INFO;
                        currentShowTaskInfo = systemTaskInfos;
                        break;
                    case R.id.rb_show_user_process:
                        LogUtil.d("显示用户应用进程");
                        taskInfoShowMode = SHOW_USER_TASK_INFO;
                        currentShowTaskInfo = userTaskInfos;
                        break;
                    default:break;
                }
                adapter.notifyDataSetChanged(); // 刷新列表
                dialog.dismiss(); // 关闭对话框
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show(); // 显示对话框
    }

    /**
     * 用于进入设置页面
     * @param view
     */
    public void enterSetting(View view){
        Intent intent = new Intent(TaskManagerActivity.this, TaskSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.notifyDataSetChanged(); // 刷新列表
    }
}
