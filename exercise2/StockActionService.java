import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.UUID;

public class StockActionService extends Thread{
    Socket client;
    Broker broker;
    BufferedReader fromClient;
    DataOutputStream toClient;
    boolean connected = true;

    StockActionService(Socket client, Broker b){
        this.client = client;
        broker = b;
    }

    @Override
    public void run (){
        String firstLine;
        System.out.println("Thread started: " + this); // Display Thread-ID

        try{
            fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            toClient = new DataOutputStream (client.getOutputStream());

            while(connected){
                StringBuilder requestString = new StringBuilder();
                firstLine = fromClient.readLine();
                int contentLength = Integer.parseInt(firstLine);
                for(int i = 0; i<contentLength; i++){
                    requestString.append((char)fromClient.read());
                }

                Message received = new Message(requestString.toString());
                System.out.println("Received: \n" + received.toString());

                Message response = executeMethod(received);
                toClient.writeBytes(response.getEncapsulated());
            }

            fromClient.close();
            toClient.close();
            client.close();
            System.out.println("Thread ended: " + this);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Message executeMethod(Message msg)
    {
        String method = msg.getHeader().get(MessageHeaderField.requestMethod);
        try
        {
            switch(method)
            {
                case "addSellActionToList":
                    StockAsk stockAsk = new StockAsk();
                    stockAsk.hydrateFromServerString(msg.toString());
                    broker.addSellActionToList(stockAsk);
                    return (new Message()).setHeaderField(MessageHeaderField.responseStatus, "Your ask has been taken into account");
                case "addBuyActionToList":
                    StockBid stockBid = new StockBid();
                    stockBid.hydrateFromServerString(msg.toString());
                    broker.addBuyActionToList(stockBid);
                    return (new Message()).setHeaderField(MessageHeaderField.responseStatus, "Your bid has been taken into account");
                case "getSellActionByUuid":
                    StockAsk sa;

                    try {
                        sa = broker.getSellActionByUuid(UUID.fromString(msg.getBody().get("uuid")));
                    } catch (Exception e) {
                        sa = null;
                    }

                    if(sa == null){
                        return (new Message()).setHeaderField(MessageHeaderField.responseStatus, "NOT FOUND");
                    } else {
                        return sa.toMessage().setHeaderField(MessageHeaderField.responseStatus, "OK");
                    }
                case "getBuyActionByUuid":
                    StockBid sb;

                    try {
                        sb = broker.getBuyActionByUuid(UUID.fromString(msg.getBody().get("uuid")));
                    } catch (Exception e) {
                        sb = null;
                    }

                    if(sb == null){
                        return (new Message()).setHeaderField(MessageHeaderField.responseStatus, "NOT FOUND");
                    } else {
                        return sb.toMessage().setHeaderField(MessageHeaderField.responseStatus, "OK");
                    }
                default:
                    toClient.writeBytes("Action not allowed. Please restart the service" + '\n');
                    connected = false;
                    break;
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        return null;
    }
}