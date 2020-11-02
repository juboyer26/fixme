package fix;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Sell implements ChainHandler{
    private ChainHandler nextInChain;

    public void setNextChain(ChainHandler nextChain) {
        nextInChain = nextChain;
    }

    public void execute(String request, String instrumentType, String amount, String price, Socket marketSocket, String dataSentToRouter, Instrument gold, Instrument silver) throws IOException {
        if(request.equals("2")){
            System.out.println("we recieved a sell order");
            if (instrumentType.equals("1") && (Integer.parseInt(price) < gold.getPrice())){//GOLD
//                    System.out.println("GOLD BEFORE BUY BACK FROM BROKER: "+ gold.getQuantity());
//                    gold.setQuantity(gold.getQuantity()+ Integer.parseInt(amount));
//                    System.out.println("GOLD AFTER BUY BACK: " + gold.getQuantity());

                    dataSentToRouter = "accepted";
                    OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                    PrintWriter printWriter = new PrintWriter(os);
                    printWriter.println(dataSentToRouter);
                    printWriter.flush();
                }
                else if (instrumentType.equals("2") && (Integer.parseInt(price) < silver.getPrice())){//SILVER
//                    System.out.println("SILVER BEFORE BUY BACK: "+ silver.getQuantity());
//                    silver.setQuantity(silver.getQuantity()+ Integer.parseInt(amount));
//                    System.out.println("SILVER AFTER BUY BACK: " + silver.getQuantity());

                    dataSentToRouter = "accepted";
                    OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                    PrintWriter printWriter = new PrintWriter(os);
                    printWriter.println(dataSentToRouter);
                    printWriter.flush();
                }
                else {
                    dataSentToRouter = "rejected|market will only buy instrument for a price lower than current market price... Gold price: R"
                    + gold.getPrice() +"...Silver Price: R" + silver.getPrice();
                    OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                    PrintWriter printWriter = new PrintWriter(os);
                    printWriter.println(dataSentToRouter);
                    printWriter.flush();
                }
        }
        else {
            System.out.println("Only works for BUY and SELL");
        }
    }
}
