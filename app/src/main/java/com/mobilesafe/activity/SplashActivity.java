package com.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.utils.FileUtils;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;
import com.mobilesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    private final int ENTER_HOME = 0;
    private final int SHOW_UPDATE_DIALOG = 1;
    private final int URL_ERROR = 2;
    private final int NETWORK_ERROR = 3;
    private final int JSON_ERROR = 4;
    private String description = null;
    private String apkurl = null;
    private SharedPreferences sharedPreferences; // 定义一个私有共享属性

    @BindView(R.id.tv_splash_version)
    TextView tvSplashVersion;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_splash_progress)
    TextView tvSplashProgress;

    private boolean isRunning = true;
    private int count = 0;//用于计数
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENTER_HOME://无版本更新，直接进入主页
                    enterHome();
                    PromptManager.showShortToast(getBaseContext(), "无版本更新，直接进入主页");
                    break;
                case SHOW_UPDATE_DIALOG:
                    PromptManager.showShortToast(getBaseContext(), "有版本更新，开始升级对话框");
                    showUpdateDialog(); // 弹出对话框
                    break;
                case URL_ERROR://URL错误，直接进入主页
                    PromptManager.showShortToast(getBaseContext(), "URL错误，直接进入主页");
                    enterHome();
                    break;
                case NETWORK_ERROR://网络错误，直接进入主页
                    PromptManager.showShortToast(getBaseContext(), "网络错误，直接进入主页");
                    enterHome();
                    break;
                case JSON_ERROR://JSON解析错误，直接进入主页
                    PromptManager.showShortToast(getBaseContext(), "JSON解析错误，直接进入主页");
                    enterHome();
                    break;
            }

        }
    };

    /**
     * 显示是否升级对话框
     */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this); // 需要绑定当前activity
        builder.setTitle("提示升级");

//        builder.setCancelable(false); // 不允许 取消对话框
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            if (dialog!=null)
                dialog.dismiss(); // 隐藏对话框
            enterHome();// enter Home Activity
            }
        });
        builder.setMessage(description);
        builder.setPositiveButton("确认更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 存在SD卡
                    String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/new.apk"; // 目标路径
                    File file = new File(targetPath); // 实例化一个file类，用于逻辑处理文件
                    if (null!=file && file.exists()){
                        LogUtil.i("删除。。。"+file.getName());
                        file.delete();
                    }

                    FinalHttp finalHttp = new FinalHttp();// 实例化一个FinalHttp实例
                    HttpHandler handler = finalHttp.download(apkurl, targetPath,
                            true, new AjaxCallBack<File>() {
                                @Override
                                public void onStart() {
                                    super.onStart();
                                    LogUtil.i("HttpHandler onStart...");
                                }

                                @Override
                                public void onLoading(long count, long current) {
                                    super.onLoading(count, current);
                                    LogUtil.i("HttpHandler onLoading...");
                                    tvSplashProgress.setVisibility(View.VISIBLE);
                                    tvSplashProgress.setText("下载进度：" + (current * 100 /count));
                                }

                                @Override
                                public void onSuccess(File file) {
                                    super.onSuccess(file);
                                    LogUtil.i("HttpHandler onSuccess...");
                                    tvSplashProgress.setText(file==null?"null":file.getAbsoluteFile().toString());

                                    FileUtils.installApk(getBaseContext(), file);//启动安装流程
                                }

                                @Override
                                public void onFailure(Throwable t, int errorNo, String strMsg) {
                                    super.onFailure(t, errorNo, strMsg);
                                }
                            });
                } else {
                    PromptManager.showShortToast(getBaseContext(), "SD卡不存在，请插入SD卡再尝试");
                }
            }
        });
        builder.setNegativeButton("下次更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// 关闭对话框
                enterHome(); // 进入主页
            }
        });
        builder.show(); // 显示对话框
    }

    /**
     * 进入主页
     */
    private void enterHome() {
        LogUtil.i("this is enterHome");
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

        copyDB(); // 拷贝数据库文件到path目录，便于查询电话归属地

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE);
        boolean isUpdate = sharedPreferences.getBoolean(getResources().getString(R.string.update), false);
        if (isUpdate){ // 启动自动更新
            checkUpdate();// check weather has new version in server
        }else { // 关闭自动更新
            mHandler.postDelayed(new Runnable() {// 延迟2s，进入主页面
                @Override
                public void run() {
                    enterHome();
                }
            }, 2000);
        }

        tvSplashVersion.setText("版本号：" + getVersionName());

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(1000); // 设置动画时长
        findViewById(R.id.rl_root_splash).startAnimation(alphaAnimation);
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private String getVersionName() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    private void updataProgressBar() {
        new Thread() {
            @Override
            public void run() {

                while (isRunning) {
                    try {
                        Thread.sleep(1000);
                        count++;
                        Message message = new Message();// new a Message object
                        message.what = 0;
                        message.arg1 = count;
                        mHandler.sendMessage(message);// through mHandler send a message to the MessageQueue
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (count >= 10) isRunning = false; // out of while
                }
            }
        }.start();
    }

    /**
     * 检测服务器版本是否有更新
     */
    private void checkUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Message message = new Message(); // 创建一个Message
                try {
                    URL url = new URL(getString(R.string.server_url)); // new a URL 链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // 创建一个连接
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    int code = connection.getResponseCode(); // get ResponseCode
                    if (code == HttpURLConnection.HTTP_OK) {
                        int length = connection.getContentLength();//内存长度
                        LogUtil.i("length = " + length);
                        InputStream is = connection.getInputStream();// get the InputStream
                        String result = StreamTools.readFromStream(is); // 从输入流中读取结果
                        LogUtil.i("result = " + result);

                        // close 流和连接
                        is.close();
                        connection.disconnect();

                        JSONObject jsonObject = new JSONObject(result);// 解析一个json对象，从json里面读取版本信息
                        String version = jsonObject.getString("version");
                        description = jsonObject.getString("description");
                        apkurl = jsonObject.getString("apkurl");

                        LogUtil.i("version =" + version + ";" + description + ";" + apkurl);

                        //检验是否有更新
                        if (getVersionName().equals(version)) {
                            // 假如版本一致，直接启动主页面
                            message.what = ENTER_HOME;
                        } else {
                            // 有更新的话，启动下载
                            message.what = SHOW_UPDATE_DIALOG;
                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    message.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = NETWORK_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = JSON_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    long diff = endTime - startTime; // 计算时间差值

                    if (diff < 2000) {
                        try {
                            Thread.sleep(2000 - diff);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    mHandler.sendMessage(message);// 添加message到队列中
                }
            }
        }).start();

    }

    /**
     * 把address.db拷贝到data/data/com.mobilesafe/files/address.db
     */
    private void  copyDB(){
        // 假如数据库已经存在了，则不再拷贝该数据库
        try {
            File file = new File(getFilesDir(), "address.db");
            if (file.exists() && file.length()>0){
                LogUtil.i("file address.db is exists");
            }else {
                // 开始从assets目录下拷贝数据库到
                LogUtil.i("copyDB is running");
                InputStream is = getAssets().open("address.db");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int len = 0;
                while ( (len = is.read(buf, 0, buf.length)) >= 0){
                    fos.write(buf, 0, len);
                }
                fos.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
