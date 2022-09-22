package vip.floatationdevice.guilded4j_personal;

import cn.hutool.json.JSONObject;
import com.google.common.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import vip.floatationdevice.guilded4j_personal.event.ConnectionClosedEvent;
import vip.floatationdevice.guilded4j_personal.event.UnknownGuildedEvent;
import vip.floatationdevice.guilded4j_personal.event.WelcomeEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * For receiving WebSocket messages.
 * Not recommended to use it alone without G4JPClient.
 */
public class G4JPWSClient extends WebSocketClient
{
    public final EventBus eventBus = new EventBus();
    public boolean verboseOutput = false;
    private EventBus bus;
    private Thread hb;

    public G4JPWSClient(String token, String cookies, String serverId)
    {
        super(URI.create("wss://www.guilded.gg/ws/?teamId={0}&jwt=undefined&guildedClientId={1}&EIO=3&transport=websocket".replace("{0}", serverId).replace("{1}", token)));
        setConnectionLostTimeout(30);
        addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0");
        addHeader("Cookie", cookies);
    }

    public G4JPWSClient(String token, String cookies)
    {
        super(URI.create("wss://www.guilded.gg/ws/?jwt=undefined&guildedClientId={1}&EIO=3&transport=websocket".replace("{1}", token)));
        setConnectionLostTimeout(30);
        addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:99.0) Gecko/20100101 Firefox/99.0");
        addHeader("Cookie", cookies);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        if(verboseOutput)
            System.out.println("Connection opened\n" + handshakedata.getHttpStatus() + ": " + handshakedata.getHttpStatusMessage());
        hb = new Thread("Heartbeat")
        {
            @Override
            public void run()
            {
                try
                {
                    for(; ; )
                    {
                        sleep(10000L);
                        if(verboseOutput) System.out.println("Sending heartbeat");
                        send("2");
                    }
                }
                catch(InterruptedException e) {}
            }
        };
        hb.start();
    }

    @Override
    public void onMessage(String message)
    {
        if(verboseOutput) System.out.println(message);
        if(message.startsWith("0{")) // welcome event
        {//FIXME: doesnt post WelcomeEvent
            JSONObject j = new JSONObject(message.substring(1));
            if(verboseOutput) System.out.println(j.toStringPretty());
            bus.post(new WelcomeEvent(this));
        }
        else if(message.startsWith("42[")) // normal event
        {
            JSONObject j = new JSONObject(message.substring(message.indexOf(',') + 1, message.length() - 1));
            if(verboseOutput) System.out.println(j.toStringPretty());
            try
            {// eventBus.post(new xxxEvent(this, j)) in reflection
                Class<?> eventClass = Class.forName("vip.floatationdevice.guilded4j_personal.event." + j.getStr("type"));
                Constructor<?> constructor = eventClass.getConstructor(Object.class, JSONObject.class);
                Object event = constructor.newInstance(this, j);
                eventBus.post(event);
            }
            catch(ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                  InstantiationException | IllegalAccessException e)
            {
                System.err.println(e);
                eventBus.post(new UnknownGuildedEvent(this, j).setReason(e));
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        if(verboseOutput) System.out.println("Connection closed\nCode: " + code + ", reason: " + reason);
        hb.interrupt();
        bus.post(new ConnectionClosedEvent(this, code, reason, remote));
    }

    @Override
    public void onError(Exception ex)
    {
        throw new RuntimeException(ex);
    }

    @Override
    public void connect()
    {
        super.connect();
    }

    public G4JPWSClient setVerbose(boolean v)
    {
        verboseOutput = v;
        return this;
    }

    public G4JPWSClient setEventBus(EventBus eventBus)
    {
        if(eventBus == null) throw new IllegalArgumentException();
        bus = eventBus;
        return this;
    }
}
