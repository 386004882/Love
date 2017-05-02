package com.example.jiji.love.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.example.jiji.love.Config;
import com.example.jiji.love.JavaBean.Contract;
import com.example.jiji.love.Utils.HttpCallbackListener;
import com.example.jiji.love.Utils.HttpUtils;
import com.example.jiji.love.Utils.NetWorkUtils;

import org.json.JSONArray;
import org.json.JSONObject;


public class MsgReceiveService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        receiveMessage();

        return super.onStartCommand(intent, flags, startId);
    }

    //接收新消息
    private void receiveMessage() {
        if (NetWorkUtils.isNetworkConnected(getApplicationContext())) {
            //开启子线程接循环收数据
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //根据用户名获取数据
                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                    String username = sp.getString("username", "");
                    if (username != "") {
                        String address = Config.ServeAddress + "/getmessage.php?name=" + username;
                        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
                            @Override
                            public void onFinish(String reponse) {
                                try {
                                    if (reponse != null && reponse != "") {
                                        //解析json数据
                                        JSONArray jsonArray = new JSONArray(reponse);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String sender = jsonObject.getString("sender");
                                            String content = jsonObject.getString("content");
                                            String time = jsonObject.getString("time");
                                            String id = jsonObject.getString("id");
                                            // LogUtil.d("TestServiec", "onFinish: sender:" + sender + "-----content:" + content
                                            //       + "-----time:" + time);
                                            //传递数据给activity
                                            Intent intent = new Intent();
                                            Contract contract = new Contract(id, sender, content, time);
                                            intent.putExtra("contract", contract);
                                            intent.setAction("com.love.service.MsgReceiveService");
                                            sendBroadcast(intent);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(MsgReceiveService.this, "服务请求失败", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }).start();
        }
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int fiveSecon = 2000;
        long triggerAtTime = SystemClock.elapsedRealtime() + fiveSecon;
        Intent i = new Intent(this, MsgReceiveService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

    }
}
