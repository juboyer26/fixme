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
            if (instruction == null) {
                br.close();
                break;
            }
            System.out.println("Instruction recieved: " + instruction);
            extract(instruction);

            if (orderType.equals("1")) { //BUY
                if (instrumentType.equals("1")) {//GOLD
                    if ((gold.getQuantity() >= Integer.parseInt(amount)) && (Integer.parseInt(price) >= gold.getPrice())) {
                        System.out.println("Gold Order: " + amount);
                        System.out.println("Gold Quantity Available: " + gold.getQuantity());
                        System.out.println("You can buy Gold");

                        //send response to router
//                        dataSentToRouter = "8=FIX.4.2|35=D|49=BROKERID|56=MARKETID|52=TIME|54=1|38=500|44=100|39=1|41=1|CHECKSUM";
                        dataSentToRouter = "accepted";
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();

                        //update instrument
                        gold.setQuantity(gold.getQuantity() - Integer.parseInt(amount));
                        System.out.println("Gold Quantity after execution: " + gold.getQuantity());
                    } else {
                        dataSentToRouter = "rejected|quantity not available or price is too low";
                        System.out.println("REJECTED GOLD ORDER");
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();
                    }
                }
                if (instrumentType.equals("2")) {//SILVER
                    if ((silver.getQuantity() >= Integer.parseInt(amount)) && (Integer.parseInt(price) >= silver.getPrice())) {
                        System.out.println("Silver Order: " + amount);
                        System.out.println("Silver Quantity Available: " + silver.getQuantity());
                        System.out.println("You can buy silver");

                        //send response to router
                        dataSentToRouter = "accepted";
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();

                        //update instrument
                        silver.setQuantity(silver.getQuantity() - Integer.parseInt(amount));
                        System.out.println("Quantity after execution: " + silver.getQuantity());
                    } else {
                        dataSentToRouter = "rejected|quantity not available or price is too low";
                        System.out.println("REJECTED SILVER ORDER");
                        OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                        PrintWriter printWriter = new PrintWriter(os);
                        printWriter.println(dataSentToRouter);
                        printWriter.flush();
                    }
                }
            }else if (orderType.equals("2")){//SELL
                if (instrumentType.equals("1")){//GOLD
                    System.out.println("GOLD BEFORE SELL: "+ gold.getQuantity());
                    gold.setQuantity(gold.getQuantity()+ Integer.parseInt(amount));
                    System.out.println("GOLD INCREASED: " + gold.getQuantity());

                    dataSentToRouter = "accepted";
                    System.out.println("EXE GOLD SALE");
                    OutputStreamWriter os = new OutputStreamWriter(marketSocket.getOutputStream());
                    PrintWriter printWriter = new PrintWriter(os);
                    printWriter.println(dataSentToRouter);
                    printWriter.flush();
                }
                if (instrumentType.equals("2")){//SILVER
                    System.out.println("SILVER BEFORE SELL: "+ silver.getQuantity());
                    silver.setQuantity(silver.getQuantity()+ Integer.parseInt(amount));
                    System.out.println("SILVER INCREASED: " + silver.getQuantity());

                    dataSentToRouter = "accepted";
                    System.out.println("EXEC SILVER SALE");
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
//        String input = "8=FIX.4.2|35=D|49=BROKERID|56=MARKETID|52=TIME|54=1|38=500|44=100|39=1|41=1|CHECKSUM";
        //8=FIX.4.2|35=D|49=304635|56=908440|52=2020-10-26T12:59:17.767104900Z|54=1|40=1|38=1|44=1|39=1|10=59
        String[] msgs = input.split("\\|");
        System.out.println("X: " + input);

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


