package com.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.mobilesafe.R;
import com.mobilesafe.bean.User;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Setup3Activity extends BaseSetupActivity {

    private final int REQUEST_CONTACT = 0; // 请求返回联系人
    private EditText etPhoneNum;
    private String safeNum = null; // 安全号码

    @Override
    protected int initLayout() {
        return R.layout.activity_setup3;
    }

    @Override
    protected void initView() {
        etPhoneNum = retrieveView(R.id.et_phone_num);

        safeNum = sp.getString("safeNum", null);
        if ( null == safeNum)
            PromptManager.showShortToast(this, "没有设置安全号码，请设置");
        else
            etPhoneNum.setText(safeNum); // 设置到输入编辑框
    }

    public void next(View view) {

        if (TextUtils.isEmpty(safeNum)){
            PromptManager.showShortToast(this, "安全号码不能为空");
            return;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safeNum", safeNum);
        editor.commit(); // 提交修改

        Intent i = new Intent(this, Setup4Activity.class);
        startActivity(i);
        finish();
        // 此方法在finish和startActivity后调用
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    public void prev(View view) {
        Intent i = new Intent(this, Setup2Activity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    @Override
    public void showNext() {
        next(null);
    }

    @Override
    public void showPrev() {prev(null);
    }

    /**
     * 跳转到选择联系人页面
     * @param view
     */
    public void startSelectContactActivity(View view){
        Intent i = new Intent(this, SelectContactActivity.class);
//        startActivity(i);
        startActivityForResult(i, REQUEST_CONTACT); // 启动页面时请求返回一个联系人数据
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case REQUEST_CONTACT:
                LogUtil.i("resultCode = "+resultCode); // 打印返回码
                LogUtil.i("data = "+data.getStringExtra("SelectContact"));
                User user = (User) data.getSerializableExtra("user");
                safeNum = user.getNumber().replace("-", "");
                etPhoneNum.setText(safeNum); // 设置到输入编辑框
                break;
            default:break;
        }
    }
}
