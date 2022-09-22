package vip.floatationdevice.guilded4j_personal.event;

import cn.hutool.json.JSONObject;

import java.util.EventObject;

public class GuildedEvent extends EventObject
{
    String eventType, serverId, channelId;
    JSONObject raw;

    public GuildedEvent(Object source, JSONObject raw)
    {
        super(source);
        this.raw = raw;
        eventType = raw.getStr("type");
        serverId = raw.getStr("teamId");
        channelId = raw.getStr("channelId");
    }

    public String getEventType(){return eventType;}

    public String getServerId(){return serverId;}

    public String getChannelId(){return channelId;}

    public JSONObject getRaw(){return raw;}
}
