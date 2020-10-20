package market;

// import market.MarketServer;

public class Main {
   
    public static void main(String[] args) {
        try {
            MarketServer.runServer();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}