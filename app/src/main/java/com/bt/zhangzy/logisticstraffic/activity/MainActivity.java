package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.bt.zhangzy.logisticstraffic.R;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
//                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
//                startActivity(new Intent(MainActivity.this, LocationActivity.class));
                if(User.getInstance().isFirstOpen()) {
                    startActivity(WelcomeActivity.class);
                }else{
                    startActivity(HomeActivity.class);
                }
                finish();
                return true;
            }
        }).sendEmptyMessageDelayed(0,2000);

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
