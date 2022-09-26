package vip.floatationdevice.guilded4j_personal.event;

import cn.hutool.json.JSONObject;

import java.util.EventObject;

public class WelcomeEvent extends EventObject
{
    private final String sid;
    private final int pingInterval, pingTimeout;

    public WelcomeEvent(Object o, JSONObject j)
    {
        super(o);
        this.sid = j.getStr("sid");
        this.pingInterval = j.getInt("pingInterval");
        this.pingTimeout = j.getInt("pingTimeout");
    }

    public String getSid(){return sid;}

    public int getPingInterval(){return pingInterval;}

    public int getPingTimeout(){return pingTimeout;}
}
