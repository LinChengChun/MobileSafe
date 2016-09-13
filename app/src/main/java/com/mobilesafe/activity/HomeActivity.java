package com.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.mobilesafe.R;
import com.mobilesafe.adapter.GridViewAdapter;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.MyItem;
import com.mobilesafe.utils.MD5Utils;
import com.mobilesafe.utils.PromptManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/2.
 */
public class HomeActivity extends BaseActivity {
    @BindView(R.id.gv_list_home)
    GridView gvListHome;
    private EditText et_setup_pwd;
    private EditText et_setup_confirm;
    private Button btn_sure;
    private Button btn_cancel;
    private AlertDialog dialog;

    private List<MyItem> mListData = null; // 定义一个集合用于存储数据
    private GridViewAdapter adapter = null; // 定义一个适配器
    private SharedPreferences sharedPreferences = null; // 用于保存密码

    @Override
    protected int initLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

    }

    @Override
    protected void initListener() {
        gvListHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent; // 定义一个意图
                switch (position){
                    case 0:// 进入手机防盗页面
                        showLoadingFindDialog();
                        break;
                    case 1:// 进入通讯卫士页面
                        intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2:// 进入软件管理页面
                        intent = new Intent(HomeActivity.this, AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 3:// 进入程序管理页面
                        intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 4:// 进入流量统计页面
                        intent = new Intent(HomeActivity.this, TrafficManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 5:// 进入手机杀毒页面
                        intent = new Intent(HomeActivity.this, AntiVirusActivity.class);
                        startActivity(intent);
                        break;
                    case 7: // 启动高级工具页面
                        intent = new Intent(HomeActivity.this, AtoolsActivity.class);
                        startActivity(intent);
                        break;
                    case 8:// 启动设置页面
                        intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        mListData = new ArrayList<MyItem>(); // new a list object
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE);

        String[] names = getResources().getStringArray(R.array.functions); // 获取中文名称数组
        int[] ids = {
                R.drawable.safe,
                R.drawable.callmsgsafe,
                R.drawable.app,
                R.drawable.taskmanager,
                R.drawable.netmanager,
                R.drawable.trojan,
                R.drawable.sysoptimize,
                R.drawable.atools,
                R.drawable.settings};
        for (int i=0;i<names.length;i++){
            MyItem myItem = new MyItem(names[i], ids[i]);
            mListData.add(myItem);
        }
        adapter = new GridViewAdapter(this, mListData); // 创建一个适配器
        gvListHome.setAdapter(adapter);
    }

    /**
     * 显示手机防盗对话框
     */
    private void showLoadingFindDialog() {
        // 判断是否设置过密码
        if (isSetupPassword()){
            // 已经设置密码了，弹出的是输入对话框
            showEnterDialog();
        }else {
            // 没有设置密码，弹出的是设置密码对话框
            showSetupPasswordDialog();
        }
    }

    /**
     * 显示进入对话款，用于已经设置好密码
     */
    private void showEnterDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(HomeActivity.this, R.layout.dialog_input_password, null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_sure = (Button) view.findViewById(R.id.btn_sure);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();// 关闭对话框
            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_setup_pwd.getText().toString().trim();// 去空格
                String savePassword = sharedPreferences.getString("password", null);// 取出加密后的密码
                if (TextUtils.isEmpty(password)){
                    PromptManager.showShortToast(HomeActivity.this, "密码为空");
                    return;
                }
                if (savePassword.equals(MD5Utils.md5Password(password))){
                    PromptManager.showShortToast(HomeActivity.this, "输入正确");
                    dialog.dismiss();// 关闭对话框
                    enterLostFindActivity();// 进入手机防盗页面
                }else {
                    PromptManager.showShortToast(HomeActivity.this, "密码输入有误");
                    et_setup_pwd.setText("");
                    return;
                }
            }
        });
//        builder.setView(view);// 自定义一个对话框布局文件
//        dialog = builder.show();
        dialog = builder.create();
        dialog.setView(view,0,0,0,0);// 解决低版本 对话框无法覆盖满页面问题
        dialog.show();
    }

    /**
     * 是否已经设置密码
     * @return boolean
     */
    private boolean isSetupPassword(){
        String password = sharedPreferences.getString("password", null);
        return !TextUtils.isEmpty(password);
    }

    /**
     * 显示设置密码对话框
     */
    private void showSetupPasswordDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_sure = (Button) view.findViewById(R.id.btn_sure);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();// 关闭对话框
            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_setup_pwd.getText().toString().trim();// 去空格
                String confirm = et_setup_confirm.getText().toString().trim();
                if ( TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)){// 已经有密码
                    PromptManager.showShortToast(HomeActivity.this, "密码输入为空，请重新输入");
                    return;
                }

                if (password.equals(confirm)){
                    // 一致的话，就保存密码，关闭对话框，进入防盗页面
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", MD5Utils.md5Password(password));// 保存加密后的密码
                    editor.commit();
                    PromptManager.showShortToast(HomeActivity.this, "密码输入正确，进入手机防盗页面");
                    dialog.dismiss();// 关闭对话框
                    enterLostFindActivity();// 进入手机防盗页面
                }else {
                    PromptManager.showShortToast(HomeActivity.this, "密码输入不一致，请重新输入");
                    return;
                }
            }
        });

//        builder.setView(view);// 自定义一个对话框布局文件
//        dialog = builder.show();
        dialog = builder.create();
        dialog.setView(view,0,0,0,0);
        dialog.show();
    }

    private void enterLostFindActivity(){
        Intent i = new Intent(HomeActivity.this, LostFindActivity.class);
        startActivity(i);
    }
}
