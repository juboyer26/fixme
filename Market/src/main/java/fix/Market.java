package fix;

import java.io.*;
import java.net.Socket;

public class Market {
    private static String amount;
    private  static String price;
    private static String orderType;
    private static String instrumentType;
    private static int[] id;

    public static void main(String[] args) throws Exception {
        System.out.println("Establishing connection to Router...");
        Socket marketSocket = new Socket("localhost", 5001);
        BufferedReader br = new BufferedReader(new InputStreamReader(marketSocket.getInputStream()));
        Instrument gold = new Instrument("GOLD", 1000, 1200);
        Instrument silver = new Instrument("SILVER", 2000, 800);
        String dataSentToRouter;
        boolean idRecieved = true;

        while (idRecieved) {
            System.out.println("waiting for broker to connect...");
            try {
                id = readId(marketSocket);
                System.out.println("broker id " + id[0]);
                System.out.println("market id " + id[1]);
                break;
            } catch (Exception e) {
                System.out.println("Error getting id");
                System.exit(1);
            }
        }


        while (true) {
            String instruction = br.readLine();
            if (instruction.equals("quit")){
                br.close();
                break;
            }
            extract(instruction);

            if (orderType.equals("1")) { //BUY
                if (instrumentType.equals("1")) {//GOLD
                    if ((gold.getQuantity() >= Integer.parseInt(amount)) && (Integer.parseInt(price) >= gold.getPrice())) {
                        System.out.println("Gold Quantity Available: " + gold.getQuantity());

                        dataSentToRouter = "accepted";
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();

                        gold.setQuantity(gold.getQuantity() - Integer.parseInt(amount));
                        System.out.println("Gold Quantity after sale to broker: " + gold.getQuantity());
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
                        System.out.println("Silver Quantity Available: " + silver.getQuantity());

                        dataSentToRouter = "accepted";
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();

                        silver.setQuantity(silver.getQuantity() - Integer.parseInt(amount));
                        System.out.println("Silver Quantity available after sale to broker : " + silver.getQuantity());
                    } else {
                        dataSentToRouter = "rejected|quantity not available or price is too low + \n Quantity available: "+ silver.getQuantity()
                                + " @ Price of R" + silver.getPrice();
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();
                    }
                }
            }else if (orderType.equals("2")){//SELL
                if (instrumentType.equals("1") && (Integer.parseInt(price) < gold.getPrice())){//GOLD
                    System.out.println("GOLD BEFORE BUY BACK FROM BROKER: "+ gold.getQuantity());
                    gold.setQuantity(gold.getQuantity()+ Integer.parseInt(amount));
                    System.out.println("GOLD AFTER BUY BACK: " + gold.getQuantity());

                    dataSentToRouter = "accepted";
                    OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                    PrintWriter printWriter = new PrintWriter(os);
                    printWriter.println(dataSentToRouter);
                    printWriter.flush();
                }
                else if (instrumentType.equals("2") && (Integer.parseInt(price) < silver.getPrice())){//SILVER
                    System.out.println("SILVER BEFORE BUY BACK: "+ silver.getQuantity());
                    silver.setQuantity(silver.getQuantity()+ Integer.parseInt(amount));
                    System.out.println("SILVER AFTER BUY BACK: " + silver.getQuantity());

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
        }
        br.close();

    }
    public static void extract(String input){
        //8=FIX.4.2|35=D|49=304635|56=908440|52=2020-10-26T12:59:17.767104900Z|54=1|40=1|38=1|44=1|39=1|10=59
        String[] msgs = input.split("\\|");

        amount = msgs[7].substring(3); //Quantity     38
        price = msgs[8].substring(3); // Price      44
        orderType = msgs[5].substring(3);//1BUY OR 2SELL    54
        instrumentType = msgs[6].substring(3); //1GOLD 2Silver   40
    }

    public static int[] readId(Socket s) {
        int[] ids = {0, 0};
        System.out.println("Reading ...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            ids[0] = Integer.parseInt(br.readLine());
            ids[1] = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (ids);
    }
}


