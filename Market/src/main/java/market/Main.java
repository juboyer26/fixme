package market;

import market.controller.*;

public class Main {
   
    public static void main(String[] args) {

        MarketController market = new MarketController();
        try {
            market.connect();
        } catch (Exception e) {
            System.out.println("errorrrr  : " + e);
        }
    }

    // id=100000|8=FIX.4.2|35=8|39=8|50=100000|49=100000|56=100004|10=105|
    //https://corp-web.b2bits.com/fixanet/doc/html/html/464a7a55-67c0-483d-95e4-18aa083715b1.htm#:~:text=in%20a%20FIX%20message%20are,with%20the%20%228%3DFIX.
    
// following this tutorial
    // http://www.java2s.com/Tutorials/Java/Java_Network/0080__Java_Network_Asynchronous_Socket_Channels.htm
}