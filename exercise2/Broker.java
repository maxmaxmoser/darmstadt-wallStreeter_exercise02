import java.util.ArrayList;
import java.util.UUID;

public class Broker {
    private ArrayList<StockBid> buyActionList;
    private ArrayList<StockAsk> sellActionList;
    private FileLogger fileLogger;

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

}
