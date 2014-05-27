package fr.tvbarthel.apps.adaptilo.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class Launcher {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Simple Server Launcher");

        //create roles
        final ArrayList<String> roles = new ArrayList<String>();
        roles.add("controller");
        roles.add("filed");

        //create server
        final SingleGameServer server = new SingleGameServer(new InetSocketAddress(8887), "testGame", roles);

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
            } else {
                System.out.println("sendToAll " + in);
                server.sendToAll(in);
            }
        }
    }
}
