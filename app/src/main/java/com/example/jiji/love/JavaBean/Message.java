package com.example.jiji.love.JavaBean;

import java.io.Serializable;

/**
 * Created by jiji on 2017/4/16.
 */

public class Message implements Serializable {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private int type;
    private String sender;
    private String recever;
    private String time;
    private String content;

    public Message(String sender, String content, int type, String time) {
        this.sender = sender;
        this.time = time;
        this.type = type;
        this.content = content;
    }
    public Message(String sender, String content, int type) {
        this.sender = sender;
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecever() {
        return recever;
    }

    public void setRecever(String recever) {
        this.recever = recever;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
