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
        System.out.println(brID);
        out.println(brID);
        out.flush();
        System.out.println(mrID);
        out.println(mrID);
        out.flush();
    }
    public void SendString(String reply){
        System.out.println(reply);
        out.println(reply);
        out.flush();
    }

    public static String readMsg(Socket s, boolean close){
        String str = null;
        System.out.println("Waiting for data from broker");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            str = br.readLine();
            if (close) {
                br.close();
                System.out.println("buffer closed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return(str);
    }

    public void close(){

    }
    public void SendStringToAll() throws IOException {
        if (router.pair.containsValue(i)) {
            ServerConnection mr = router.MarketList.get(i);
            ServerConnection br = router.BrokerList.get(i);
            String msg;
            if (!idSent){
                br.SendID(router.brokerid, router.marketid);
                mr.SendID(router.brokerid, router.marketid);
                msg = br.readMsg(br.socket, close);
                mr.SendString(msg);//loops through array of connections and sends the string to them all
                msg = mr.readMsg(mr.socket, close);
                br.SendString(msg);
                idSent = true;
                if (msg.equals("quit")){
                    close = true;
                    System.out.println("broker ID: "+router.brokerid+"closed");
                }
            }
            else{
                msg = br.readMsg(br.socket, close);
                int begin = msg.indexOf("49=") + 3;
                int end = begin + 6;
                tid = msg.substring(begin, end);
                bid = Integer.parseInt(tid);
                begin = msg.indexOf("56=") + 3;
                end = begin + 6;
                tid = msg.substring(begin, end);
                mid = Integer.parseInt(tid);
                System.out.println("Broker ID in Server is: "+bid+" Market ID inn server is: "+mid);
                if(router.pair.containsKey(bid)){
                    mr.SendString(msg);
                    msg = mr.readMsg(mr.socket, close);
                    br.SendString(msg);
                    if (msg.equals("quit")){
                        System.out.println("broker ID: "+router.brokerid+"closed");
                        close = true;
                    }
                }
            }
        }
    }

    @lombok.SneakyThrows//if lombok is red than make sure you open structure and add lombok to your library
    public void run(){//overwriting the run() function in thread
        os = new OutputStreamWriter(socket.getOutputStream());
        out = new PrintWriter(os);
        System.out.println("do you even enter");
        if (router.marketid > 99999){
            router.pair.put(router.brokerid, router.i);
            System.out.println(i);
            i = router.i;
            router.i++;
            idSent = false;
            while (true) {
                System.out.println("apparently you do");
                if(close){
                    router.MarketList.get(i).close();
                    router.MarketList.remove(i);
                    router.BrokerList.get(i).close();
                    router.BrokerList.remove(i);
                    readMsg(socket,close);
                    os.close();
                    out.close();
                    System.out.println("Everything closed");
                    break;
                }
                SendStringToAll();
            }
        }
    }

}
