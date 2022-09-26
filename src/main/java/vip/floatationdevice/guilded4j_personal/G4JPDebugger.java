package vip.floatationdevice.guilded4j_personal;

import com.google.common.eventbus.Subscribe;
import vip.floatationdevice.guilded4j_personal.event.ConnectionClosedEvent;
import vip.floatationdevice.guilded4j_personal.event.GuildedEvent;
import vip.floatationdevice.guilded4j_personal.event.WelcomeEvent;

import java.util.Scanner;

public class G4JPDebugger
{
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        G4JPClient c;
        System.out.println("Do you have cookies? Press ENTER if no, or paste it here if you have");
        String cookies = scanner.nextLine();
        String serverId = "zE8VxJNR";
        if(!cookies.isEmpty())
            c = new G4JPClient(cookies);
        else
        {
            String email, password;
            System.out.print("Email: ");
            email = scanner.nextLine();
            System.out.print("Password: ");
            password = scanner.nextLine();
            c = new G4JPClient(email, password);
            System.out.println("Logging in. Please wait");
            cookies = c.login();
            System.out.println("Login successful. Your cookies:\n" + cookies + "\nYou can save the line above for future use");
        }
        c.registerEventListener(new EventListener());
        c.setVerbose(true);
        c.connectWebSocket();
    }

    static class EventListener
    {
        @Subscribe
        public void onWelcomeEvent(WelcomeEvent e)
        {
            System.out.println("Logged in\nsid: " + e.getSid() +
                    "\npingInterval: " + e.getPingInterval() +
                    "\npingTimeout: " + e.getPingTimeout()
            );
        }

        @Subscribe
        public void onDisconnect(ConnectionClosedEvent e)
        {
            System.out.println("Connection closed\nCode: " + e.getCode() + ", reason: " + e.getReason());
        }

        @Subscribe
        public void onGuildedEvent(GuildedEvent e)
        {
            System.out.println(e.getClass() + "\n" + e.getRaw().toString());
        }
    }
}
