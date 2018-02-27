import java.util.ArrayList;
import java.util.UUID;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class Broker {
    private ArrayList<StockBid> buyActionList;
    private ArrayList<StockAsk> sellActionList;
    private FileLogger fileLogger;
    private static final int port = 8080;

    public Broker(){
        buyActionList = new ArrayList<StockBid>();
        sellActionList = new ArrayList<StockAsk>();
        fileLogger = new FileLogger("logFile.csv");
    }

    public ArrayList<StockBid> getBuyActionList() {
        return buyActionList;
    }

    public ArrayList<StockAsk> getSellActionList() {
        return sellActionList;
    }

    public void setBuyActionList(ArrayList<StockBid> buyActionList) {
        this.buyActionList = buyActionList;
    }

    public void setSellActionList(ArrayList<StockAsk> sellActionList) {
        this.sellActionList = sellActionList;
    }

    public void addSellActionToList(StockAsk action){
        action.setStatus(StockActionStatus.PENDING);
        sellActionList.add(action);
        establishTrade();
    }

    public void addBuyActionToList(StockBid action){
        action.setStatus(StockActionStatus.PENDING);
        buyActionList.add(action);
        establishTrade();
    }

    public StockAsk getSellActionByUuid(UUID uuid)
    {
        for(StockAsk action : sellActionList)
        {
            if(action.getUUID().equals(uuid))
            {
                return action;
            }
        }
        return null;
    }

    public StockBid getBuyActionByUuid(UUID uuid)
    {
        for(StockBid action : buyActionList)
        {
            if(action.getUUID().equals(uuid))
            {
                return action;
            }
        }
        return null;
    }

    public StockBid getHighestBid(){
        double max = buyActionList.get(0).getPrice();
        StockBid maxBid = buyActionList.get(0);

        for(int i = 0; i < buyActionList.size(); i++) {
            StockBid bid = buyActionList.get(i);
            if(bid.getPrice() > max)
            {
                max = bid.getPrice();
                maxBid = bid;
            }
        }
        return maxBid;
    }

    public StockAsk getLowestAsk(){
        double min = sellActionList.get(0).getPrice();
        StockAsk minAsk = sellActionList.get(0);

        for(int i = 0; i < sellActionList.size(); i++) {
            StockAsk ask = sellActionList.get(i);
            if(ask.getPrice() > min)
            {
                min = ask.getPrice();
                minAsk = ask;
            }
        }
        return minAsk;
    }

    public void establishTrade()
    {
        StockBid highestBid = getHighestBid();
        StockAsk lowestAsk = getLowestAsk();

        if((highestBid.getStatus() == StockActionStatus.OK) || (lowestAsk.getStatus() == StockActionStatus.OK))
        {
            return;
        }

        if(highestBid.getPrice() >= lowestAsk.getPrice())
        {
            highestBid.setStatus(StockActionStatus.OK);
            lowestAsk.setStatus(StockActionStatus.OK);

            //Adds the transaction details to the logfile.csv
            fileLogger.writeToFile(lowestAsk.getStock().getName().name(),String.valueOf(highestBid.getPrice()));


        }
    }

    public String searchAction(String dMin, String dMax, String actionName) {
        return fileLogger.searchLogFile(dMin, dMax, actionName);
    }

    public static void main (String [] args) {
        try {

            WebServer webServer = new WebServer(port);

            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();

            phm.addHandler( "Broker", Broker.class);
            xmlRpcServer.setHandlerMapping(phm);

            XmlRpcServerConfigImpl serverConfig =
                    (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
            // serverConfig.setEnabledForExtensions(true);
            // serverConfig.setContentLengthOptional(false);

            webServer.start();

            System.out.println("The Calculator Server has been started..." );

        } catch (Exception exception) {
            System.err.println("JavaServer: " + exception);
        }
    }

}
