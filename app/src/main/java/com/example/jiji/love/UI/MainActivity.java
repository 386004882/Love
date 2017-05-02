package com.example.jiji.love.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.jiji.love.R;
import com.example.jiji.love.Service.MsgReceiveService;
import com.example.jiji.love.Utils.NetWorkUtils;

public class MainActivity extends BaseActivity {
    /**
     * 本页面无作用，仅提供一个一个按钮跳转到聊天页面
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 因为需要根据用户信息从服务器获取数据
         * 现在模拟数据
         * 之后只需将用户的账号存入SharedPreferences中即可
         */
        //生成用户信息
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", "zhangsan");
        editor.putString("password", "123456");
        editor.apply();

        //开启接收消息服务
        Intent intent = new Intent(MainActivity.this, MsgReceiveService.class);
        if (NetWorkUtils.isNetworkConnected(getApplicationContext())) {
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //按钮点击事件
    public void openActivity(View v) {
        Intent intent = new Intent(MainActivity.this, ContractsListActivity.class);
        startActivity(intent);
    }
}
