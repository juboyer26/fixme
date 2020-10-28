package market.controller;

import market.model.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.*;
import market.model.*;

public class MarketController {
    public static int dstId;
    // private static Attachment attach;
    private static final String fixv = "8=FIX.4.2";
    static Asset silver = new Asset("Silver", 1000, 10.5);
    static Asset gold = new Asset("Gold", 1000, 10.5);

    public MarketController() {
        System.out.println("Market Online!");
    }

    public void contact() throws UnknownHostException, IOException {
        String ip = "localhost";
        int port = 5001;
        Socket s = new Socket(ip, port);
        update();
        while (true) {
            String str = null;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                str = br.readLine();
                if (str.contains("8=FIX.4.2"))
                    System.out.println(str);
                else
                    System.out.println("Market id: " + str);
                runReq(s, str);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void update() {
        System.out.println();
        System.out.println("Assets");
        System.out.println("Gold price: 10.5 Quantity: " + gold.totalAmount);
        System.out.println("Silver price: 10.5 Quantity: " + silver.totalAmount);
        System.out.println();
    }

    public static void runReq(Socket s, String str) {
        String info[] = str.split("\\|");

        if (str.equals("quit")) {
            System.exit(1);
        }
        String msgType = "";
        String msg = "";
        String processType = "";
        String price = "";
        String quantity = "";
        String assetType = "";
        String asset = "";

        if (info[0].equals("8=FIX.4.2")) {
            for (String data : info) {
                if (data.contains("49="))
                    dstId = Integer.parseInt(data.split("=")[1]);
                else if (data.contains("44="))
                    price = data.split("=")[1];
                else if (data.contains("35="))
                    msgType = data.split("=")[1];
                else if (data.contains("54="))
                    processType = data.split("=")[1];
                else if (data.contains("38="))
                    quantity = data.split("=")[1];
                else if (data.contains("55="))
                    assetType = data.split("=")[1];
                else if (data.contains("40="))
                    asset = data.split("=")[1];

            }
            msg = processReq(msgType, processType, price, quantity, assetType, asset);
            System.out.println("Message: " + msg);
            sendMsg(s, msg);
            System.out.println("Waiting for new order...\n");
            update();
        }
    }

    public static void sendMsg(Socket s, String msg) {
        try {
            OutputStreamWriter os = new OutputStreamWriter(s.getOutputStream());
            PrintWriter out = new PrintWriter(os);
            out.println(msg);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readMsg(Socket s) {
        String str = null;
        System.out.println("Waiting for transactions...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            str = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (str);
    }

    public static String processReq(String msgType, String processType, String price, String quantity, String assetType,
            String asset) {
        if (asset.equals("1")) {
            if (msgType.equals("D") && processType.equals("1") && gold.getPrice() <= Integer.parseInt(price))
                return fixMessage(1, Integer.parseInt(quantity), Integer.parseInt(price), asset);
            else if (msgType.equals("D") && processType.equals("2") && gold.getPrice() <= Integer.parseInt(price))
                return fixMessage(2, Integer.parseInt(quantity), Integer.parseInt(price), asset); // sell
            else
                return fixMessage(0, Integer.parseInt(quantity), Integer.parseInt(price), asset); // reject broker
        } else {
            if (msgType.equals("D") && processType.equals("1") && silver.getPrice() <= Integer.parseInt(price))
                return fixMessage(1, Integer.parseInt(quantity), Integer.parseInt(price), asset);
            else if (msgType.equals("D") && processType.equals("2") && silver.price <= Integer.parseInt(price))
                return fixMessage(2, Integer.parseInt(quantity), Integer.parseInt(price), asset); // sell
            else
                return fixMessage(0, Integer.parseInt(quantity), Integer.parseInt(price), asset); // reject broker
        }
    }

    public static String fixMessage(int processType, int quantity, int price, String asset) {
        String msg = "";
        if (asset.equals("1")) {
            if (processType == 1) {
                if (gold.buyAsset(quantity))
                    msg = "accepted";
                else
                    msg = "rejected";
            } else if (processType == 2) {
                if (gold.sellAsset(quantity, (double) price))
                    msg = "accepted";
                else
                    msg = "rejected";
            } else {
                msg = "rejected";
            }
        } else {
            if (processType == 1) {
                if (silver.buyAsset(quantity))
                    msg = "accepted";
                else
                    msg = "rejected";
            } else if (processType == 2) {
                if (silver.sellAsset(quantity, (double) price))
                    msg = "accepted";
                else
                    msg = "rejected";
            } else {
                msg = "rejected";
            }
        }
        return msg;
    }

}