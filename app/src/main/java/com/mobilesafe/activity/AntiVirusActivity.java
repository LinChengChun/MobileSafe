package com.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.db.dao.AntiVirusDao;
import com.mobilesafe.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cclin on 2016/9/13.
 */
public class AntiVirusActivity extends BaseActivity {

    private static final int SCANING = 0;
    private static final int FINISH = 1;
    @BindView(R.id.iv_scan)
    ImageView ivScan;

    @BindView(R.id.pb_scan)
    ProgressBar pbScan;

    @BindView(R.id.tv_scan_status)
    TextView tvScanStatus;

    @BindView(R.id.ll_container)
    LinearLayout llContainer;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANING: // 扫描过程中，更新ui
                    ScanInfo info = (ScanInfo)(msg.obj);
                    tvScanStatus.setText("正在扫描: "+info.name);

                    TextView tv = new TextView(getApplicationContext());
                    if (info.isAntiVirus){
                        tv.setTextColor(Color.RED);
                        tv.setText("发现病毒:"+info.name);
                    }else {
                        tv.setTextColor(Color.BLACK);
                        tv.setText("扫描安全:"+info.name);
                    }
                    llContainer.addView(tv, 0); // 加载过程界面

                    break;
                case FINISH:
                    tvScanStatus.setText("扫描完毕");
                    ivScan.clearAnimation(); // 取消动画
                    break;
            }

        }
    };
    private PackageManager pm; // 包管理器

    @Override
    protected int initLayout() {
        return R.layout.activity_anti_virus;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE); // 持续旋转
        ivScan.startAnimation(rotateAnimation);

        scanVirus(); // 开启线程，进行扫描
//        pbScan.setMax(100);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i=0; i<=100; i++){
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    pbScan.setProgress(i);
//                }
//            }
//        }).start();
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
    }

    /**
     * 扫描病毒
     */
    private void scanVirus(){
        pm = getPackageManager(); // 获取包管理器
        tvScanStatus.setText("正在初始化8核杀毒引擎。。。");
        new Thread(){
            @Override
            public void run() {
                List<PackageInfo> packageInfos = pm.getInstalledPackages(0); // 获取系统已安装的应用包信息
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int progress = 0; // 当前进度
                pbScan.setMax(packageInfos.size()); // 设置进度条最大值
                for (PackageInfo info: packageInfos){ // 扫描每一个包信息
                    String dataDir = info.applicationInfo.dataDir; // 获取 应用安装目录路径
                    String sourceDir = info.applicationInfo.sourceDir;// 获取apk文件完整的路径
                    LogUtil.d("dataDir: "+dataDir);
                    LogUtil.d("sourceDir: "+sourceDir);
                    ScanInfo scanInfo = new ScanInfo(); // 保存信息
                    scanInfo.packname = info.packageName;
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();

                    String md5 = getFileMd5(sourceDir); // 获取 文件 的特征码
                    LogUtil.d(info.applicationInfo.loadLabel(pm)+": "+md5);

                    // 查询md5病毒信息
                    if (AntiVirusDao.isVirus(md5)){
                        // 发现病毒
                        scanInfo.isAntiVirus = true;
                    }else {
                        // 扫描安全
                        scanInfo.isAntiVirus = false;
                    }
                    Message msg = Message.obtain();
                    msg.obj = scanInfo;
                    msg.what = SCANING;
                    mHandler.sendMessage(msg);

                    progress++;
                    pbScan.setProgress(progress); // 更新进度条
                }

                Message msg = Message.obtain();
                msg.what = FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 获取文件的MD5值，识别文件特征码
     * @param path
     * @return
     */
    private String getFileMd5(String path){
        StringBuffer sb = new StringBuffer();
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);

            // 获取到一个MD5加密算法的信息摘要器
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            } // 每个字节进行哈希摘要计算一遍
            byte[] result = digest.digest(); // 拿到当前文件MD5签名信息/哈希摘要

            // 进行整型变量到十六进制哈希码字符串转换
            for (byte b: result){
                int num = b & 0xff;
                String str = Integer.toHexString(num);
                if (str.length() == 1)
                    sb.append("0");
                sb.append(str);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    class ScanInfo{
        String packname;
        String name;
        boolean isAntiVirus;
    }
}
