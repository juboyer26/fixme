package market.model;

import market.model.*;
import market.controller.*;
import java.nio.charset.*;
import java.nio.channels.*;
import java.io.*;

import market.controller.MarketController;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
  @Override
  public void completed(Integer result, Attachment attach) {
    if (result == -1)
      {
        attach.mainThread.interrupt();
        System.out.println("Server shutdown unexpectedly, Market going offline...");
        return ;
      }
      if (attach.isRead) {
        attach.buffer.flip();
        Charset cs = Charset.forName("UTF-8");
        int limits = attach.buffer.limit();
        byte bytes[] = new byte[limits];
        attach.buffer.get(bytes, 0, limits);
        String msg = new String(bytes, cs);
        if (attach.clientId == 0)
        {
          attach.clientId = Integer.parseInt(msg);
          System.out.println("Server Responded with Id: " + attach.clientId);
          attach.isRead = false;
          attach.client.read(attach.buffer, attach, this);
          return ;
        }
        else
          System.out.println("Server Responded: "+ msg);
        
        
        attach.buffer.clear();
        msg = MarketController.processRequest(msg);
        if (msg.contains("bye")) {
          attach.mainThread.interrupt();
          return;
        }
        try {
          System.out.println("\nMarket Response: "+ msg);
        } catch (Exception e) {
         
        }
        byte[] data = msg.getBytes(cs);
        attach.buffer.put(data);
        attach.buffer.flip();
        attach.isRead = false;
        attach.client.write(attach.buffer, attach, this);
      }else {
        attach.isRead = true;
        attach.buffer.clear();
        attach.client.read(attach.buffer, attach, this);
      }
  }
  @Override
  public void failed(Throwable e, Attachment attach) {
    e.printStackTrace();
  }
  private String getTextFromUser() throws Exception{
    System.out.print("Please enter a  message  (Bye  to quit):");
    BufferedReader consoleReader = new BufferedReader(
        new InputStreamReader(System.in));
    String msg = consoleReader.readLine();
    return msg;
  }
}