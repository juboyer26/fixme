package fix;
import java.net.*;
import java.io.*;
import java.time.Instant;
import java.util.*;
import fix.InitInstruments;

public class Broker {

    private static int type = 0;
    private static int qty = 0;
    private static int price =0;
    private static String message;
    private static int[] id;
    private static String checksum;
    private static Scanner scanner;
    private static int choice = 0;
    public static void main(String args[])throws Exception{

        String ip = "localhost";
        int port = 5000;
        Socket s = new Socket(ip, port);
        String fixMsg;
        String fixStart;

        ArrayList<Instruments> instruments = new ArrayList<Instruments>();
        InitInstruments initializer = new InitInstruments();
        int count = 0;
        boolean ask = true;
        boolean idRecieved = true;

        //get id
        while(idRecieved){
            System.out.println("waiting for broker to connect...");
            try{
                id = readId(s);
                System.out.println("broker id " + id[0]);
                System.out.println("market id " + id[1]);
                break;
            }catch (Exception e){
                System.out.println("Error getting id");
                System.exit(1);
            }
        }

        //list of all brokers assets
        instruments = initializer.setInstruments(); //init instrument
        fixStart = "8=FIX.4.2|";

        while(ask){
             scanner = new Scanner(System.in);  // Create a Scanner object
            System.out.println("\nDo you want to buy or sell?\n" +
                    "1. Buy\n" +
                    "2. Sell\n" +
                    "3. Exit\n");


            if(!checkOption()){
                continue;
            }

            if(choice == 1){
                System.out.println("These are the assets available on the market, please select an asset you want to buy\n");
                for(int i = 0; i < instruments.size(); i++) {
                    String inst = i+1 + ". Name: " + instruments.get(i).getName();
                    System.out.println(inst);
                }
                if(!checkOption()){
                    continue;
                }
                fixMsg =  buy(choice, instruments);
                if(fixMsg == null){
                    continue;
                }
                fixMsg = fixStart + fixMsg;
                checksum = "|10=" + checksum(fixMsg);
                fixMsg = fixMsg + checksum;
                System.out.println("FIX MESSAGE " + fixMsg);
                sendMsg(s, fixMsg);

                //wait for reply then do XYZ
                String response = readMsg(s);
                if(response.equals("accepted")){
                    instruments.get(type).setQuantity(instruments.get(type).getQuantity() + qty);
                    System.out.println("accepted");
                }else if(response.equals("rejected")){
                    continue;
                }else{
                    System.out.println("Error, invalid response");
                    continue;
                }

            }else if(choice == 2){
                System.out.println("These are your available assets, please select an asset you want to sell");
                for(int i = 0; i < instruments.size(); i++) {
                    String inst = i+1 + ". Name: " + instruments.get(i).getName()+ ", Quantity Available: " + instruments.get(i).getQuantity();
                    System.out.println(inst);
                }
                if(!checkOption()){
                    continue;
                }
                fixMsg = sell(choice, instruments);
                if(fixMsg == null){
                    continue;
                }
                fixMsg = fixStart + fixMsg;
                checksum = "|10=" + checksum(fixMsg);
                fixMsg = fixMsg + checksum;
                System.out.println("FIX MESSAGE " + fixMsg);
                sendMsg(s, fixMsg);

                //wait for reply then do XYZ
                String response = readMsg(s);
                if(response.equals("accepted")){
                    instruments.get(type).setQuantity(instruments.get(type).getQuantity() - qty);
                    System.out.println("accepted");
                    //add values to broker
                }else if(response.equals("rejected")){
                    continue;
                }else{
                    System.out.println("Error, invalid response");
                    continue;
                }

            }else if(choice == 3){
                ask = false;
                System.out.println("Exiting...");
                sendMsg(s, "quit");
                s.close();
                System.exit(1);
            }else{
                System.out.println("Invalid option");
                continue;
            }
        }
    }

    public static String buy(int choice, ArrayList<Instruments> instruments){
         scanner = new Scanner(System.in);

        if (choice == 1) {
            type = 0;
            System.out.println("Please select the amount of " + instruments.get(0).getName() + " you would like to buy");
            //qty = scanner.nextInt();
            checkQty();
            if (qty == 0){
                return null;
            }
            System.out.println("Enter your price");
            //price = scanner.nextInt();
            checkPrice();
            if (price == 0){
                return null;
            }
            message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+1+"|40="+1+"|38="+qty+"|44="+price+"|39=1";
        }else if (choice == 2) {
            type = 1;
            System.out.println("Please select the amount of " + instruments.get(1).getName() + " you would like to buy");
            //qty = scanner.nextInt();
            checkQty();
            if (qty == 0){
                return null;
            }
            System.out.println("Enter your price");
            //price = scanner.nextInt();
            checkPrice();
            if (price == 0){
                return null;
            }
            message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+1+"|40="+2+"|38="+qty+"|44="+price+"|39=1";
        }else if (choice == 3) {
            type = 2;
            System.out.println("Please select the amount of " + instruments.get(2).getName() + " you would like to buy");
            //qty = scanner.nextInt();
            checkQty();
            if (qty == 0){
                return null;
            }
            System.out.println("Enter your price");
           // price = scanner.nextInt();
            checkPrice();
            if (price == 0){
                return null;
            }
            message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+1+"|40="+3+"|38="+qty+"|44="+price+"|39=1";
        }
        else if (choice == 4) {
            type = 3;
            System.out.println("Please select the amount of " + instruments.get(3).getName() + " you would like to buy");
            //qty = scanner.nextInt();
            checkQty();
            if (qty == 0){
                return null;
            }
            System.out.println("Enter your price");
            //price = scanner.nextInt();
            checkPrice();
            if (price == 0){
                return null;
            }
            message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+1+"|40="+4+"|38="+qty+"|44="+price+"|39=1";
        }else{
            System.out.println("Invalid asset selected");
            return null;
        }
        return  message;
    }

    public static String sell(int choice, ArrayList<Instruments> instruments){
        scanner = new Scanner(System.in);

        if (choice == 1) {
            type = 0;
            System.out.println("Please select the amount of " + instruments.get(0).getName() + " you would like to sell");
            //qty = scanner.nextInt();
            checkQty();
            if(qty > instruments.get(0).getQuantity()){
                System.out.println("You do not have enough " + instruments.get(0).getName() + " please buy some first");
            }else if(qty <= 0){
                System.out.println("Please select a valid amount");
                return null;
            }else{
                System.out.println("Enter your price");
                //price = scanner.nextInt();
                checkPrice();
                if (price == 0){
                    return null;
                }
                message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+2+"|40="+4+"|38="+qty+"|44="+price+"|39=1";
            }
        }else if (choice == 2) {
            type = 1;
            System.out.println("Please select the amount of " + instruments.get(1).getName() + " you would like to sell");
            //qty = scanner.nextInt();
            checkQty();
            if(qty > instruments.get(1).getQuantity()){
                System.out.println("You do not have enough " + instruments.get(1).getName() + " please buy some first");
            }else if(qty <= 0){
                System.out.println("Please select a valid amount");
                return null;
            }else{
                System.out.println("Enter your price");
                //price = scanner.nextInt();
                checkPrice();
                if (price == 0){
                    return null;
                }
                message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+2+"|40="+4+"|38="+qty+"|44="+price+"|39=1";
            }
        }else if (choice == 3) {
            type = 2;
            System.out.println("Please select the amount of " + instruments.get(2).getName() + " you would like to sell");
            //qty = scanner.nextInt();
            checkQty();
            if(qty > instruments.get(2).getQuantity()){
                System.out.println("You do not have enough " + instruments.get(2).getName() + " please buy some first");
            }else if(qty <= 0){
                System.out.println("Please select a valid amount");
                return null;
            }else{
                System.out.println("Enter your price");
                //price = scanner.nextInt();
                checkPrice();
                if (price == 0){
                    return null;
                }
                message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+2+"|40="+4+"|38="+qty+"|44="+price+"|39=1";
            }
        }
        else if (choice == 4) {
            type = 3;
            System.out.println("Please select the amount of " + instruments.get(3).getName() + " you would like to sell");
            //qty = scanner.nextInt();
            checkQty();
            if(qty > instruments.get(3).getQuantity()){
                System.out.println("You do not have enough " + instruments.get(3).getName() + " please buy some first");
            }else if(qty <= 0){
                System.out.println("Please select a valid amount");
                return null;
            }else{
                System.out.println("Enter your price");
               // price = scanner.nextInt();
                checkPrice();
                if (price == 0){
                    return null;
                }
                message = "35=D|49="+id[0]+"|56="+id[1]+"|52="+ Instant.now().toString()+"|54="+2+"|40="+4+"|38="+qty+"|44="+price+"|39=1";
            }
        }else{
            System.out.println("Invalid asset selected");
            return null;
        }
        return message;
    }

    public static void sendMsg(Socket s, String msg){
        try {
            OutputStreamWriter os = new OutputStreamWriter(s.getOutputStream());
            PrintWriter out = new PrintWriter(os);
            out.println(msg);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readMsg(Socket s){
        String str = null;
        System.out.println("Reading ...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            str = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return(str);
    }

    public static int[] readId(Socket s){
        int[] ids = {0, 0};
        System.out.println("Reading ...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            ids[0] = Integer.parseInt(br.readLine());
            ids[1] = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return(ids);
    }

    public static int checksum(String msg){
        int checksum = 0;
        for (int i = 0; i < msg.length(); i++) {
            checksum += msg.charAt(i);
        }
        return checksum % 256;
    }

    public static boolean checkOption(){
        try{
            choice = scanner.nextInt();
        }catch (InputMismatchException e){
            System.out.println("Inavlid option");
            return false;
        }
        return true;
    }

    public static void checkQty(){
        try{
            qty = scanner.nextInt();
            if(qty <= 0){
                System.out.println("Invalid qty");
                qty = 0;
            }
        }catch (InputMismatchException e){
            System.out.println("Invalid qty");
            qty = 0;
        }
    }

    public static void checkPrice(){
        try{
            price = scanner.nextInt();
            if(price <= 0){
                System.out.println("Invalid Price");
                price = 0;
            }
        }catch (InputMismatchException e){
            System.out.println("Inavlid Price");
            price = 0;
        }
    }
