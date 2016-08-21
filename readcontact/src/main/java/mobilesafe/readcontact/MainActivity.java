package mobilesafe.readcontact;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView lvSelectContact;
    private List<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("Trim", "onCreate...");
        lvSelectContact = (ListView) findViewById(R.id.lv_select_contact);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_CONTACTS权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        }

        list = getContactInfo();
        lvSelectContact.setAdapter(new SimpleAdapter(getApplicationContext(), list,
                R.layout.item_user_layout,
                new String[]{"name", "num"}, new int[]{R.id.tv_user_name, R.id.tv_user_number} ));
    }

    private List<Map<String, String>> getContactInfo(){

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ContentResolver resolver = getContentResolver();

        // raw_contacts uri
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");// 联系人表
        Uri uriData = Uri.parse("content://com.android.contacts/data");//

        Cursor cursor = resolver.query(uri,new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()){
            Log.i("Trim", "count = "+cursor.getColumnCount());
            String contact_id = cursor.getString(0);
            if (!TextUtils.isEmpty(contact_id)){

                Cursor dataCursor = resolver.query(uriData, new String[]{"data1","mimetype"},
                        "contact_id=?", new String[]{contact_id}, null);
                Map<String, String> map = new HashMap<String, String>(); // 定义一个Map集合

                while (dataCursor.moveToNext()){
                    String data1 = dataCursor.getString(0);
//                    String result = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                    String mimetype = dataCursor.getString(1);

                    Log.i("Trim", "ColumnIndex = "+dataCursor.getColumnIndex("data1"));
                    Log.i("Trim", "ColumnIndex = "+dataCursor.getColumnIndex("mimetype"));

                    Log.i("Trim", data1+":"+mimetype);

                    if ("vnd.android.cursor.item/name".equals(mimetype)){
                        map.put("name", data1);
                    }else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        map.put("num", data1);
                    }
                }
                list.add(map);// 添加到list集合中
                dataCursor.close();
            }
        }
        cursor.close();
        return list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    public void doNext(int requestCode, int[] grantResults){
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.i("Trim", "申请权限成功");
            } else {
                // Permission Denied
                Log.i("Trim", "申请权限失败");
            }
        }
    }
}
