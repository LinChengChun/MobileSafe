package com.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.utils.PromptManager;
import com.mobilesafe.utils.SmsUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Administrator on 2016/8/6.
 */
public class AtoolsActivity extends BaseActivity{

    ProgressDialog progressDialog = null;

    @Override
    protected int initLayout() {
        return R.layout.activity_atools;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 点击事件，进入号码归属地查询页面
     * @param view
     */
    public void startNumberAddressQuery(View view) {
        Intent intent = new Intent(AtoolsActivity.this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    /**
     * 点击事件，短信备份
     * @param view
     */
    public void smsBackup(View view) {
        progressDialog = new ProgressDialog(AtoolsActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在备份短信");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsUtils.backupSms(AtoolsActivity.this, new SmsUtils.BackUpCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                            progressDialog.setMax(max); // 设置最大值
                        }
                        @Override
                        public void onSmsBackup(int progress) {
                            progressDialog.setProgress(progress); // 更新进度条
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PromptManager.showShortToast(AtoolsActivity.this, "备份成功");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PromptManager.showShortToast(AtoolsActivity.this, "备份失败");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss(); // 进度条对话框消失
                }
            }
        }).start();
    }

    /**
     * 点击事件，短信还原
     * @param view
     */
    public void smsRestore(View view) {
        try {
            SmsUtils.restoreSms(AtoolsActivity.this, false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
