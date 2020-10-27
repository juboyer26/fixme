package fix;

import java.io.*;
import java.net.Socket;

public class ServerConnection extends Thread{
    Socket socket;
    Router router;
    OutputStreamWriter os;
    PrintWriter out;
    String tid;
    int mid;
    int bid;
    boolean idSent = false;
    int i;
    boolean close = false;

    public ServerConnection(Socket socket, Router router){
        super("ServerConnectionThread");
        this.socket = socket;
        this.router = router;
    }

    public void SendID(int brID, int mrID){
        out.println(brID);
        out.flush();
        out.println(mrID);
        out.flush();
    }
    public void SendString(String reply){
        out.println(reply);
        out.flush();
    }

    public static String readMsg(Socket s, boolean close){
        String str = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            str = br.readLine();
            if (close) {
                br.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return(str);
    }

    public static boolean checksum(String msg){
        int checksum = 0;
        int start;
        String check;
        String substring;
        String newString;
        start = msg.indexOf("|10=");
        substring = msg.substring(start);
        newString = msg.replace(substring, "");
        for (int i = 0; i < newString.length(); i++) {
            checksum += newString.charAt(i);
        }
        start = msg.indexOf("|10=")+ 4;
        check = msg.substring(start);
        return (checksum % 256 == Integer.parseInt(check));
    }

    public void SendStringToAll() {
        if (router.pair.containsValue(i)) {
            ServerConnection mr = router.MarketList.get(i);
            ServerConnection br = router.BrokerList.get(i);
            String msg;
            if (!idSent){
                br.SendID(router.brokerid, router.marketid);
                System.out.println("Broker ID:"+ router.brokerid);
                mr.SendID(router.brokerid, router.marketid);
                System.out.println("Market ID:"+ router.marketid);
                msg = br.readMsg(br.socket, close);
                if (!msg.equals("quit") && checksum(msg)) {
                    mr.SendString(msg);
                    msg = mr.readMsg(mr.socket, close);
                    br.SendString(msg);
                }
                if (msg.equals("quit")){
                    mr.SendString(msg);
                    close = true;
                }
                idSent = true;
            }
            else{
                msg = br.readMsg(br.socket, close);
                if (!msg.equals("quit") && checksum(msg)) {
                    int begin = msg.indexOf("49=") + 3;
                    int end = begin + 6;
                    tid = msg.substring(begin, end);
                    bid = Integer.parseInt(tid);
                    begin = msg.indexOf("56=") + 3;
                    end = begin + 6;
                    tid = msg.substring(begin, end);
                    mid = Integer.parseInt(tid);
                    if (router.pair.containsKey(bid)) {
                        mr.SendString(msg);
                        msg = mr.readMsg(mr.socket, close);
                        br.SendString(msg);
                    }
                }
                if (msg.equals("quit")) {
                    mr.SendString(msg);
                    System.out.println("Broker ID: " + router.brokerid + " closed");
                    System.out.println("Market ID: " + router.marketid + " closed");
                    close = true;
                }
            }
        }
    }

    @lombok.SneakyThrows//if lombok is red than make sure you open structure and add lombok to your library
    public void run(){//overwriting the run() function in thread
        os = new OutputStreamWriter(socket.getOutputStream());
        out = new PrintWriter(os);
        if (router.marketid > 99999){
            router.pair.put(router.brokerid, router.i);
            while(router.BrokerList.size() <= router.i)
                router.i--;
            i = router.i;
            router.i++;
            idSent = false;
            while (true) {
                if(close){
                    ServerConnection mr = router.MarketList.get(i);
                    ServerConnection br = router.BrokerList.get(i);
                    readMsg(socket,close);
                    mr.socket.close();
                    router.MarketList.remove(i);
                    br.socket.close();
                    router.BrokerList.remove(i);
                    router.pair.remove(i);
                    os.close();
                    out.close();
                    System.out.println("Everything closed between Broker: "+ router.brokerid+" and Market: "+ router.marketid);
                    break;
                }
                else{
                    SendStringToAll();
                }
            }
        }
    }

}
