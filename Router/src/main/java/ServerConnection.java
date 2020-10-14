import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection extends Thread{
    Socket socket;
    Router router;
    BufferedReader in;
    OutputStreamWriter os;
    PrintWriter out;
    String str;
    public ServerConnection(Socket socket, Router router){
        super("ServerConnectionThread");
        this.socket = socket;
        this.router = router;
    }

    public void SendStringToBroker(String reply){
        out.println(reply);
        out.flush();
    }

    public void SendStringToAll(String msg){
            for(int i = 0; i < router.connections.size(); i++)
        {
            ServerConnection sc = router.connections.get(i);
            sc.SendStringToBroker(msg);//loops through array of connections and sends the string to them all
        }
    }

    @lombok.SneakyThrows
    public void run(){//overwriting the run() function in thread
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //Reading data from broker
        System.out.println("Client data: " + str);

//                String str2 = str.substring(0, 3);

        os = new OutputStreamWriter(socket.getOutputStream());
        out = new PrintWriter(os);

        while (true){
            str = in.readLine();
            SendStringToAll(str);
        }
//        in.close();
//        os.close();
//        out.close();
//        socket.close();
    }

}
