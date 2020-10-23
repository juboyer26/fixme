package fix;//package java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    //    PROGRESS
//    Checksum left
//    if words quit received from broker or market it will close
    Socket s;
    ArrayList<ServerConnection> MarketList = new ArrayList<>();
    ArrayList<ServerConnection> BrokerList = new ArrayList<>();
    int brokerid;
    int marketid;
    int i;
    HashMap<Integer, Integer> pair = new HashMap<Integer, Integer>();
    public static void main(String[] args) throws IOException {
        new Router();
    }
    public Router() throws IOException {
        while(true) {
            System.out.println("Server waiting for request\ntype quit to close fix.Router");

            try (ServerSocket broker = new ServerSocket(5000)) {
                s = broker.accept();
                marketid = 0;
                System.out.println("Broker accepted");
                ServerConnection sc = new ServerConnection(s,this);//makes using multiple brokers and markets threads at the same time//                sc.setName(i);
                sc.start();//starting thread
                BrokerList.add(sc);
                brokerid = 100000 + (int)(Math.random() * ((499999 - 100000) + 1));
            } catch (IOException e) {
                System.out.println("Broker connection to Router error: " + e.getMessage()+ "\n Make sure you connect a broker first before trying to connect a market");
            }

            try (ServerSocket market = new ServerSocket(5001)) {
                s = market.accept();
                System.out.println("Market Connected");
                ServerConnection sc = new ServerConnection(s,this);
                sc.start();
                MarketList.add(sc);
                marketid = 500000 + (int)(Math.random() * ((999999 - 500000) + 1));
            } catch (IOException e) {
                System.out.println("Broker connection to Router error: " + e.getMessage());
            }
        }
//        s.close();
//        System.out.println("Router closed because there are no brokers left running");
//        System.exit(1);
    }
}

