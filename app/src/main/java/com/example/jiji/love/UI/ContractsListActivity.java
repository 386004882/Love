package com.example.jiji.love.UI;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jiji.love.Adapter.ContractAdapter;
import com.example.jiji.love.Config;
import com.example.jiji.love.JavaBean.Contract;
import com.example.jiji.love.JavaBean.Message;
import com.example.jiji.love.R;
import com.example.jiji.love.Utils.HttpCallbackListener;
import com.example.jiji.love.Utils.HttpUtils;
import com.example.jiji.love.Utils.LogUtil;
import com.example.jiji.love.Utils.MsgDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiji on 2017/4/16.
 * 会话列表页面
 * 显示联系人名字及聊天最新的消息
 */

public class ContractsListActivity extends BaseActivity {
    private List<Contract> contractList = new ArrayList<Contract>();
    private RecyclerView contractView;
    private ContractAdapter adapter;
    private MsgDatabaseHelper dbHelper;
    private MessageReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractlist);

        dbHelper = new MsgDatabaseHelper(getApplicationContext(), "MSG.db", null, 1);

        //注册接收数据广播
        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.love.service.MsgReceiveService");
        registerReceiver(receiver, filter);


        //初始化列表
        initContract();
        contractView = (RecyclerView) findViewById(R.id.contract_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        //倒序
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        adapter = new ContractAdapter(contractList);
        contractView.setLayoutManager(manager);
        contractView.setAdapter(adapter);
    }

    //初始化列表数据
    private void initContract() {
        contractList.clear();
        //从数据库中读取数据
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Contract", null, null, null, null, null, "time");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String topMessage = cursor.getString(cursor.getColumnIndex("topMessage"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    Contract contract = new Contract(id, name, topMessage, time);
                    contractList.add(contract);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
    }


    /**
     * 接收广播后逻辑：（即有新消息）(后续加入广播在聊天页面的拦截,直接更新在消息列表)
     * 联系人表：
     * 从服务器中获取一个序列化对象，将其存入联系人列表中(内容为发送者及最新消息)
     * 判断用户是否已存在
     * Y：更新最新消息
     * N:直接插入数据库中
     * 通知adapter更新ui
     * <p>
     * 消息表：
     * 获取消息后存入sqlite中
     * 根据发送者取出消息生成列表
     */
    //TODO:广播更改为有序广播，如果当前是在聊天页面即进行拦截
    //接收消息广播
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            //获取由服务传过来的对象
            final Contract contract = (Contract) intent.getSerializableExtra("contract");

            LogUtil.d("MessageReceiver", "id:" + contract.getId() + "---name:"
                    + contract.getName() + "----content:" + contract.getTopMessage());
            //通知服务器已接收数据，可删除
            String address = Config.ServeAddress + "/receiveToDelete.php?id=" + contract.getId();
            HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String reponse) {
                    //服务器删除后，将消息数据存入SQLite中
                    if (Integer.parseInt(reponse) == 1) {
                        //插入消息表
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("sender", contract.getName());
                        values.put("type", Message.TYPE_RECEIVED);//类型为接收
                        values.put("content", contract.getTopMessage());
                        values.put("time", contract.getTime());
                        db.insert("Msg", null, values);
                        db.close();

                        //更新会话
                        if (checkUser(contract.getName())) {
                            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                            String sql = "update Contract set topMessage=\"" + contract.getTopMessage() + "\"" +
                                    ",time=\"" + contract.getTime() + "\"" +
                                    " where name=\"" + contract.getName() + "\";";
                            db1.execSQL(sql);
                            db1.close();
                        } else {
                            SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                            String sql = "insert into Contract(name,topMessage,time) " +
                                    "values(\"" + contract.getName() + "\",\"" +
                                    contract.getTopMessage() + "\",\"" + contract.getTime() + "\")";
                            db1.execSQL(sql);
                            db1.close();
                        }
                    }
                    //重新更新列表数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initContract();
                            adapter.notifyDataSetChanged();
                        }
                    });

                }

                @Override
                public void onError(Exception e) {

                }
            });

        }

        //判断会话是否已存在
        private boolean checkUser(String conName) {
            boolean flag = false;
            SQLiteDatabase seldb = dbHelper.getReadableDatabase();
            Cursor cursor = seldb.query("Contract", new String[]{"name"}, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        if (conName.equals(name)) {
                            flag = true;
                            break;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            seldb.close();
            return flag;
        }
    }

    @Override
    protected void onResume() {
        //刷新数据
        if (adapter != null) {
            initContract();
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        if (adapter != null) {
            initContract();
            adapter.notifyDataSetChanged();
        }
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        //为了不报错，先取消注册广播
        //TODO:绑定广播，让广播在外部运行
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

