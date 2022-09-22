package vip.floatationdevice.guilded4j_personal.event;

import java.util.EventObject;

public class ConnectionClosedEvent extends EventObject
{
    private final int code;
    private final String reason;
    private final Boolean remote;

    public ConnectionClosedEvent(Object source, int code, String reason, boolean remote)
    {
        super(source);
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public int getCode(){return code;}

    public String getReason(){return reason;}

    public boolean isRemote(){return remote;}
}
