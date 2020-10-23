package market.controller;

import market.model.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.concurrent.*;
import market.model.*;

public class MarketController{
    public int dstId;
    private static Attachment attach;
    private static final String fixv = "8=FIX.4.2";
    Asset gold = new Asset("Gold", 50, 10.5);
    
    public MarketController(){
        System.out.println("Market constructor");
    }

    public void connect() throws Exception{
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress("localhost", 5001);
        Future<Void> result = channel.connect(serverAddr);
        result.get();
        System.out.println("Connected");
        Attachment attach = new Attachment();
        attach.client = channel;
        attach.buffer = ByteBuffer.allocate(2048);
        attach.isRead = true;
        attach.mainThread = Thread.currentThread();

        ReadWriteHandler readWriteHandler = new ReadWriteHandler();
        channel.read(attach.buffer, attach, readWriteHandler);

        try {
            Thread.currentThread().join();
        } catch (Exception e) {  
           System.out.println("Error in Connect() : " + e);
        }
    }

    public String runReq(String str){
        String info[] = str.split("|");
        System.out.println(info);
        
        String msgType = "";
        String processType = "";
        String price = "";
        String quantity = "";
        String assetType = "";
        //id=100001☺8=FIX.4.2☺35=D☺54=1☺38=2☺44=90☺55=Gold☺50=100001☺49=100001☺56=100000☺10=199☺
        for (String data : info){
            if(data.contains("id="))
                dstId = Integer.parseInt( data.split("=")[1]);
            else if(data.contains("44="))
                price = data.split("=")[1];
            else if(data.contains("35="))
                msgType= data.split("=")[1];
            else if(data.contains("38="))
                processType= data.split("=")[1];
            else if(data.contains("54="))
                quantity= data.split("=")[1];
            else if(data.contains("55="))
                assetType= data.split("=")[1];
        }

        return processReq(msgType, processType, price, quantity, assetType);
    }

    public String processReq(String msgType, String processType,String price, String quantity, String assetType){

        //buying
        if(msgType.equals('D') && processType.equals('1') && gold.getPrice() <= Integer.parseInt(price))
            return fixMessage(1, Integer.parseInt(quantity), Integer.parseInt(price));
        else if(msgType.equals('D') && processType.equals('2') && gold.price <= Integer.parseInt(price) && gold.totalAmount - Integer.parseInt(quantity) >= 0 )
            return fixMessage(2, Integer.parseInt(quantity), Integer.parseInt(price)); //sell
        else
            return fixMessage(0, Integer.parseInt(quantity), Integer.parseInt(price)); //reject broker
            
    }

    public String fixMessage(String processType, int quantity, int price){
        String soh = "|";
        String msg = "";
        //id=100001☺8=FIX.4.2☺35=D☺54=1☺38=2☺44=90☺55=Gold☺50=100001☺49=100001☺56=100000☺10=199☺

        if(processType == "1"){
            //buying
            gold.buyAsset(quantity);
            msg = "id=" + attach.clientId + soh + fixv + soh + "35=8" + soh + "39=2" + soh + "50=" + attach.clientId
                    + soh + "49=" + attach.clientId + soh + "56=" + dstId + soh;
        }
        else if(processType == "2"){
            //selling 
            //"39=2" == filled
            gold.sellAsset(quantity, (double)price);
            msg = "id=" + attach.clientId + soh + fixv + soh + "35=8" + soh + "39=2" + soh + "50=" + attach.clientId
                    + soh + "49=" + attach.clientId + soh + "56=" + dstId + soh;
        }
        else{
            //rejected
            //"39=8" == rejected
            msg = "id=" + attach.clientId + soh + fixv + soh + "35=8" + soh + "39=8" + soh + "50=" + attach.clientId
                    + soh + "49=" + attach.clientId + soh + "56=" + dstId + soh;
        }

        return msg;
    }
    
}