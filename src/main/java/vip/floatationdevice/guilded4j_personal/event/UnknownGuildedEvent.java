package vip.floatationdevice.guilded4j_personal.event;

import cn.hutool.json.JSONObject;

public class UnknownGuildedEvent extends GuildedEvent
{
    private Exception reason = null;

    public UnknownGuildedEvent(Object source, JSONObject json){super(source, json);}

    public Exception getReason(){return this.reason;}

    public UnknownGuildedEvent setReason(Exception reason)
    {
        this.reason = reason;
        return this;
    }
}
