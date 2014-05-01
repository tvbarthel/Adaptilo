package fr.tvbarthel.apps.adaptilo.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class Launcher {

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Simple Server Launcher");
        final AdaptiloServer server = new AdaptiloServer(new InetSocketAddress(8887));
        server.start();
        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            if (in.equals("exit")) {
                System.out.println("exit");
                server.stop();
                break;
            } else {
                System.out.println("sendToAll " + in);
                server.sendToAll(in);
            }
        }
    }
}
