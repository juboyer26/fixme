package fix;

import java.io.*;
import java.net.Socket;

public class Buy implements ChainHandler {
    private ChainHandler nextInChain;


    public Buy() throws IOException {
    }


    public void setNextChain(ChainHandler nextChain) {
        nextInChain = nextChain;
    }


    public void execute(String request, String instrumentType, String amount, String price, Socket marketSocket, String dataSentToRouter, Instrument gold, Instrument silver) throws IOException {
        if(request.equals("1")){
            System.out.println("we recieved a buy order");
            if (instrumentType.equals("1")) {//GOLD
                    if ((gold.getQuantity() >= Integer.parseInt(amount)) && (Integer.parseInt(price) >= gold.getPrice())) {
//                        System.out.println("Gold Quantity Available: " + gold.getQuantity());

                        dataSentToRouter = "accepted";
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();

                        gold.setQuantity(gold.getQuantity() - Integer.parseInt(amount));
//                        System.out.println("Gold Quantity after sale to broker: " + gold.getQuantity());
                    } else {
                        dataSentToRouter = "rejected|quantity not available or price is too low. Quantity available: "+ gold.getQuantity()
                        + " @ Price of R" + gold.getPrice();
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();
                    }
            }
            if (instrumentType.equals("2")) {//SILVER
                    if ((silver.getQuantity() >= Integer.parseInt(amount)) && (Integer.parseInt(price) >= silver.getPrice())) {
//                        System.out.println("Silver Quantity Available: " + silver.getQuantity());

                        dataSentToRouter = "accepted";
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();

                        silver.setQuantity(silver.getQuantity() - Integer.parseInt(amount));
//                        System.out.println("Silver Quantity available after sale to broker : " + silver.getQuantity());
                    } else {
                        dataSentToRouter = "rejected|quantity not available or price is too low + \n Quantity available: "+ silver.getQuantity()
                                + " @ Price of R" + silver.getPrice();
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();
                    }
            }
        }
        else {
//            System.out.println("pass to next chain");
            nextInChain.execute(request, instrumentType, amount, price, marketSocket, dataSentToRouter, gold, silver);
        }
    }


}
