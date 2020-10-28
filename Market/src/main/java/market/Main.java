package market;

import market.controller.*;

public class Main {
   
    public static void main(String[] args) {

        MarketController market = new MarketController();
        try {
            market.contact();
        } catch (Exception e) {
            System.out.println("errorrrr  : " + e);
        }
    }
}