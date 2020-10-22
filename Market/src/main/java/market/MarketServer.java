package market;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MarketServer {

    public static void runServer() throws IOException, InterruptedException, ExecutionException {

        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5001);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.println("Connected");
    }
}
