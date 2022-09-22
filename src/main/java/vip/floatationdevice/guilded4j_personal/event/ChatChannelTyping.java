package vip.floatationdevice.guilded4j_personal.event;

import cn.hutool.json.JSONObject;

public class ChatChannelTyping extends GuildedEvent
{
    String userId;

    public ChatChannelTyping(Object source, JSONObject raw)
    {
        super(source, raw);
        userId = raw.getStr("userId");
    }

    public String getUserId(){return userId;}
}
