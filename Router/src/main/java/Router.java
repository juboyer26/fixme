//package java;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
//import java.util.Scanner;

public class Router {
//    TODO
// must accept incoming connections from multiple brokers and markets
//When a Broker or Market establishes the connection the Router asigns it a unique 6 digit ID and communicates the ID to them both
//     the Router will use the ID to create the routing table
//Validate the message based on the checkshum.
// Identify the destination in the routing table.
// Forward the message.
// ------------------------------------------------------------------

//    PROGRESS
//    connects the broker and market and receives messages and sends them back
//    Done!!
    Socket s;
    ArrayList<ServerConnection> connections = new ArrayList<>();
    public static void main(String[] args) {
        new Router();
    }
    public Router(){
        while(true) {
            System.out.println("Server waiting for request\ntype quit to close Router");

            try (ServerSocket broker = new ServerSocket(5000)) {
                s = broker.accept();
                System.out.println("Broker accepted");
                ServerConnection sc = new ServerConnection(s,this);//makes using multiple brokers and markets threads at the same time
                sc.start();//starting thread
                connections.add(sc);
            } catch (IOException e) {
                System.out.println("Broker connection to Router error: " + e.getMessage());
            }

            try (ServerSocket market = new ServerSocket(5001)) {
                s = market.accept();
                System.out.println("Market Connected");
                ServerConnection sc = new ServerConnection(s,this);
                sc.start();
                connections.add(sc);
            } catch (IOException e) {
                System.out.println("Broker connection to Router error: " + e.getMessage());
            }
//            if (q.toLowerCase().equals("quit"))
//                break;
        }
//        broker.close();
//        Market.close();
//        out.close();
//        in.close();
//        s.close();

    }
}
