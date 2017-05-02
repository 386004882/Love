package com.example.jiji.love.JavaBean;

import java.io.Serializable;

/**
 * Created by jiji on 2017/4/16.
 */

public class Contract implements Serializable {
    private String name;
    private String id;
    private String time;
    private String topMessage;


    public Contract() {

    }

    public Contract(String id, String name, String topMessage, String time) {
        this.topMessage = topMessage;
        this.id = id;
        this.name = name;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopMessage() {
        return topMessage;
    }

    public void setTopMessage(String topMessage) {
        this.topMessage = topMessage;
    }
}
