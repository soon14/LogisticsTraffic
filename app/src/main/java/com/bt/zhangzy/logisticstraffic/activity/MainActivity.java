package com.bt.zhangzy.logisticstraffic.activity;

import android.os.Bundle;
import android.view.View;

import com.bt.zhangzy.logisticstraffic.d.R;
import com.bt.zhangzy.logisticstraffic.app.AppParams;
import com.bt.zhangzy.logisticstraffic.app.BaseActivity;
import com.bt.zhangzy.logisticstraffic.data.User;
import com.bt.zhangzy.tools.ContextTools;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    /*    private VideoView video1;
            MediaController mediaco;*/
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        video1=(VideoView)findViewById(R.id.video);
//        mediaco=new MediaController(this);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.logo_3gp);
        video1.setVideoURI(uri);
        video1.start();
//        video1.setVideoPath("/logo.mp4");

//        File file=new File("/mnt/sdcard/通话录音/1.mp4");
//        if(file.exists()){
//            //VideoView与MediaController进行关联
//            video1.setVideoPath(file.getAbsolutePath());
//            video1.setMediaController(mediaco);
//            mediaco.setMediaPlayer(video1);
            //让VideiView获取焦点
//            video1.requestFocus();
//        }
*/
        /**
         *  //注册
         //http://192.168.1.113:8033/api/register01

         //城市列表
         //http://192.168.1.113:8033/api/values

         //货物类型
         //http://192.168.1.113:8033/api/goodstype
         */
//        if (true)
//            return;

//        new Handler(getMainLooper(), new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
////                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
////                startActivity(new Intent(MainActivity.this, LocationActivity.class));
//                loginHome();
//                return true;
//            }
//        }).sendEmptyMessageDelayed(0, 2000);

    }

    public void loginHome() {
        if (User.getInstance().isFirstOpen()) {
            startActivity(WelcomeActivity.class);
        } else {
            startActivity(HomeActivity.class);
        }
        finish();
    }


    public void onClick_Test(View view) {
        startActivity(TestActivity.class);
    }

    public void onClick_Enterprise(View view) {
        AppParams.DRIVER_APP = false;
        loginHome();
    }

    public void onClick_Driver(View view) {
        AppParams.DRIVER_APP = true;
        loginHome();
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



