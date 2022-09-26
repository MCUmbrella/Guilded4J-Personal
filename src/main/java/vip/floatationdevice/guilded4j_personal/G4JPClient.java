package vip.floatationdevice.guilded4j_personal;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.google.common.eventbus.EventBus;

import java.net.HttpCookie;

public class G4JPClient
{
    public static final String HTTP_API_URL = "https://www.guilded.gg/api";
    public final EventBus eventBus = new EventBus();
    public String homeServerId = null;
    private boolean isLoggedIn = false, verboseOutput = false;
    private G4JPWSClient ws;
    private String email = null, password = null;
    private String token = null;
    private String cookies = null;

    public G4JPClient(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public G4JPClient(String cookies)
    {
        this.cookies = cookies;
        String[] cookiesSplit = cookies.split("; ");
        for(String s : cookiesSplit)
            if(s.startsWith("guilded_mid="))
                token = s.split("guilded_mid=")[1];
        ws = new G4JPWSClient(token, cookies, eventBus)
                .setVerbose(verboseOutput);
        isLoggedIn = true;
    }

    public String login()
    {
        HttpResponse result = HttpRequest.post(HTTP_API_URL + "/login")
                .body(new JSONObject().set("email", email).set("password", password).toString())
                .timeout(10000)
                .execute();
        if(verboseOutput) System.out.println(new JSONObject(result.body()).toStringPretty());
        StringBuilder cookiesSb = new StringBuilder();
        for(HttpCookie c : result.getCookies())
        {
            cookiesSb.append(c.getName()).append('=').append(c.getValue()).append("; ");
            if(c.getName().equals("guilded_mid")) token = c.getValue();
            isLoggedIn = true;
        }
        cookiesSb.setLength(cookiesSb.length() - 2);
        cookies = cookiesSb.toString();
        ws = new G4JPWSClient(token, cookies, eventBus)
                .setVerbose(verboseOutput);
        return cookies;
    }

    public void connectWebSocket()
    {
        if(token == null && !isLoggedIn) throw new IllegalStateException();
        if(homeServerId != null)
            ws = new G4JPWSClient(token, cookies, homeServerId, eventBus)
                    .setVerbose(verboseOutput);
        ws.connect();
    }

    public void disconnectWebSocket()
    {
        if(ws != null && !ws.isClosed()) ws.close();
    }

    public G4JPWSClient getWebSocketManager()
    {

        return ws;
    }

    public G4JPClient registerEventListener(Object listener)
    {
        getWebSocketManager().eventBus.register(listener);
        return this;
    }

    public G4JPClient unregisterEventListener(Object listener)
    {
        getWebSocketManager().eventBus.unregister(listener);
        return this;
    }

    public G4JPClient setHomeServerId(String homeServerId)
    {
        this.homeServerId = homeServerId;
        return this;
    }

    public G4JPClient setVerbose(boolean v)
    {
        if(v) System.out.println("Verbose output enabled");
        verboseOutput = v;
        if(ws != null) ws.setVerbose(v);
        return this;
    }
}
