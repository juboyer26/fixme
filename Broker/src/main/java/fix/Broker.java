package fix;
import java.net.*;
import java.io.*;
import java.time.Instant;
import java.util.*;
import fix.InitInstruments;

public class Broker {

    private static int qty = 0;
    private static int price =0;
    private static String message;
    private static String[] id;
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
            id = readId(s);
            if(id[0].length() == 6){
                count++;
                if(id[1].length() == 6){
                    count++;
                    System.out.println("broker id " + id[0]);
                    System.out.println("market id " + id[1]);
                    break;
                }else{
                    System.out.println("Couldnt get market id");
                }
            }else{
                System.out.println("Couldnt get broker id");
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
                checksum = null;
                fixMsg = null;

                //wait for reply then do XYZ
                String test = readMsg(s);
                //System.out.println("returned msg " + test);
                //get message and if rejected
                //continue
                //else if accepted
                //add values to broker


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
                checksum = null;
                fixMsg = null;


                //wait for reply then do XYZ
                String test = readMsg(s);
                //System.out.println("returned msg " + test);
                //get message and if rejected
                //continue
                //else if accepted
                //add values to broker


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

    public static String[] readId(Socket s){
        String[] ids = {null, null};
        System.out.println("Reading ...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            ids[0] = br.readLine();
            ids[1] = br.readLine();
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
}