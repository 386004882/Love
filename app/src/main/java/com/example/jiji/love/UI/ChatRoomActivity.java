package com.example.jiji.love.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.example.jiji.love.Adapter.MsgAdapter;
import com.example.jiji.love.Config;
import com.example.jiji.love.JavaBean.Message;
import com.example.jiji.love.R;
import com.example.jiji.love.Utils.HttpCallbackListener;
import com.example.jiji.love.Utils.HttpUtils;
import com.example.jiji.love.Utils.MsgDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jiji on 2017/4/17.
 * 聊天界面：
 * 接收消息逻辑
 * 联网在线接收数据
 * 通过解析的json数据根据接受者写入SQLite数据库
 * 每当打开activity时:
 * 从数据库中获取数据生成列表
 */

public class ChatRoomActivity extends BaseActivity {
    private MsgAdapter adapter;
    private EditText inputText;
    private MsgDatabaseHelper msgDatabaseHelper;
    private List<Message> messageList = new ArrayList<Message>();
    private RecyclerView rv_message;
    private String name;//发送者名称


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        rv_message = (RecyclerView) findViewById(R.id.message_recyclerview);
        inputText = (EditText) findViewById(R.id.et_sendText);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name = bundle.getString("name");

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rv_message.setLayoutManager(manager);
        adapter = new MsgAdapter(messageList);


        //从数据库获取数据生成列表
        msgDatabaseHelper = new MsgDatabaseHelper(this, "MSG.db", null, 1);
        SQLiteDatabase db = msgDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query("Msg", null, "sender=?", new String[]{name}, null, null, "time");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    Message msg = new Message(name, content, cursor.getInt(cursor.getColumnIndex("type"))
                            , cursor.getString(cursor.getColumnIndex("time")));
                    messageList.add(msg);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        rv_message.setAdapter(adapter);
        //定位到最后
        rv_message.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    /**
     * 点击按钮发送消息：
     * 创建message对象，并将信息存入sqlite中
     * 插入recyclerview的adapter中，通知adapter更新列表
     * 将消息发送到服务器并写入数据库中
     */
    //点击发送消息
    public void sendMsg(View view) {
        String content = inputText.getText().toString().trim();
        int type = 1;//消息类型 发送

        //数据存入数据库
        final SQLiteDatabase db = msgDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String time = format.format(curDate);//获取系统时间
        values.put("sender", name);
        values.put("content", content);
        values.put("type", type);
        values.put("time", time);
        db.insert("Msg", null, values);
        //更新ui列表
        Message msg = new Message(name, content, type);
        messageList.add(msg);
        adapter.notifyDataSetChanged();
        //定位到最后
        rv_message.smoothScrollToPosition(adapter.getItemCount() - 1);
        inputText.setText("");
        //发送到服务器
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String username = sp.getString("username", "");
        String address = Config.ServeAddress + "/insertMessage.php?" +
                "sender=" + username + "&receiver=" + name + "&content=" + content + "&time=" + time;

        //更新外部会话数据
        String sql = "update Contract set topMessage=\"" + content + "\"" + ",time=\"" + time + "\"" +
                " where name=\"" + name + "\";";
        db.execSQL(sql);
        db.close();

        HttpUtils.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String reponse) {

            }

            @Override
            public void onError(Exception e) {

            }
        });

    }


}
