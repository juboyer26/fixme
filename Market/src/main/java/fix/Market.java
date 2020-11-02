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
        String dataSentToRouter ="";
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
            ChainHandler chainHandler1 = new Buy();
            ChainHandler chainHandler2 = new Sell();

            chainHandler1.setNextChain(chainHandler2);

            chainHandler1.execute(orderType, instrumentType, amount, price, marketSocket, dataSentToRouter, gold, silver);
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


