package fr.tvbarthel.apps.adaptilo.server;

import fr.tvbarthel.apps.adaptilo.server.models.Event;
import fr.tvbarthel.apps.adaptilo.server.models.RoleConfiguration;
import fr.tvbarthel.apps.adaptilo.server.models.enums.EventAction;
import fr.tvbarthel.apps.adaptilo.server.models.enums.EventType;
import fr.tvbarthel.apps.adaptilo.server.models.enums.MessageType;
import fr.tvbarthel.apps.adaptilo.server.models.io.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple launcher for the mario demonstration.
 */
public class Launcher_mario {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Mario Server Launcher");

        //create roles
        final List<RoleConfiguration> roles = new ArrayList<RoleConfiguration>();
        final RoleConfiguration mario = new RoleConfiguration("mario", true, true, 1);
        final EventAction action = EventAction.ACTION_ENABLE;
        final Event enableClap = new Event(EventType.CLAP, action);
        mario.addInitialMessage(new Message(MessageType.SENSOR, enableClap));
        roles.add(mario);
        roles.add(new RoleConfiguration("field", true, true, 1));

        //create server
        final SingleGameServer server = new SingleGameServer(new InetSocketAddress(8887), "mario_demo", roles);

        server.start();
        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            if (in.equals("exit")) {
                System.out.println("exit");
                server.stop();
                break;
            } else if (in.equals("enable shaker")) {
                server.enableShaker(true);
            } else if (in.equals("disable shaker")) {
                server.enableShaker(false);
            } else if (in.equals("vibrate")) {
                server.vibrate(2000l);
            } else if (in.equals("vibrate pattern")) {
                server.vibratePattern(new long[]{100, 250, 200, 250, 200, 250, 200, 250, 200, 250});
            } else if (in.startsWith("controller")) {
                String id = in.substring(in.indexOf(" ") + 1);
                server.switchController(Integer.valueOf(id));
            } else if (in.equals("enable clap")) {
                server.enableClaper(true);
            } else if (in.equals("disable clap")) {
                server.enableClaper(false);
            } else {
                System.out.println("sendToAll " + in);
                server.sendToAll(in);
            }
        }
    }
}
