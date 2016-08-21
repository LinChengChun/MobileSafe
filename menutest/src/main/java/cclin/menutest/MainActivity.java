package cclin.menutest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final String TAG = "cclin";
    private Menu currentMenu; // 全局Menu

    private Handler mHandler = new Handler(); // 定义一个Handler用于处理MessageQueue
    private int count = 0;
    private android.app.ActionBar actionBar;
    private boolean isShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = this.getActionBar();
        actionBar.setTitle("hello world");
        Log.d(TAG, "actionBar: "+actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.d(TAG, "count = "+count++);
                mHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "onDestroy: count = "+count);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu...");
        menu.add(Menu.NONE, 101, 1, "语文").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, 102, 2, "数学").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, 103, 3, "英语").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, 104, 4, "历史").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        currentMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: menu = "+menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, ""+item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:Log.d(TAG, "ItemId = "+item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openOptionsMenu() {
        Log.d(TAG, "openOptionsMenu");
        super.openOptionsMenu();
    }

    @Override
    public void closeOptionsMenu() {
        Log.d(TAG, "closeOptionsMenu");
        super.closeOptionsMenu();
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        Log.d(TAG, "onOptionsMenuClosed: menu.size() = "+menu.size());
        isShowing = false;
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu==null){
            Log.d(TAG, "onMenuOpened: menu = null");
        }else{
            Log.d(TAG, "onMenuOpened: menu.size() = "+menu.size());
        }
        isShowing = true;
        return super.onMenuOpened(featureId, menu);
    }

    public void openMenu(View view) {
        Log.d(TAG, "Button openMenu is onclicked");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "going to openOptionsMenu");
                openOptionsMenu();
            }
        },500);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                refreshMenu();
                            }
                        });
                    }
                }).start();
            }
        }, 5000);
    }

    private void refreshMenu(){
        if (isShowing){
            Log.d(TAG, "going to refreshMenu");
//                    closeOptionsMenu();
            MenuItem item = currentMenu.getItem(0);
            if (item.isVisible()){
                item.setVisible(false);
            }else {
                item.setVisible(true);
            }
        }
    }
}
