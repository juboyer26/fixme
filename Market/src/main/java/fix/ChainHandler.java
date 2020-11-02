package fix;

import java.io.IOException;
import java.net.Socket;

public interface ChainHandler {
    public void setNextChain(ChainHandler nextChain);
    public void execute(String request, String instrumentType, String amount, String price, Socket marketSocket, String dataSent, Instrument gold, Instrument silver) throws IOException;
}
