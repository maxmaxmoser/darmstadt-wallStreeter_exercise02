import java.io.IOException;

public class Trader {
    static UserInterface user = new UserInterface();
    final static String QUIT = "quit";

    public static Message getRequest() {
        String line = "";

        try {
            while(true) {
                user.output("What do you want to do ? (1) Buy/Sell (2) Check the status of one request : ");
                line = user.input(); if(line.equals(QUIT)) return null;
                if(line.equals("1")) return getStockActionRequest(getStockActionFormCLI());
                if(line.equals("2")) return getStockActionByUUIDRequest();
            }
        } catch (IOException e){ }

        return null;
    }

    public static Message getStockActionRequest(StockAction sa){
        Message toSend = sa.toMessage();

        if(sa instanceof StockAsk){
            toSend.setHeaderField(MessageHeaderField.method, "addSellActionToList");
        } else if (sa instanceof StockBid){
            toSend.setHeaderField(MessageHeaderField.method, "addSellActionToList");
        }

        return toSend;
    }

    public static Message getStockActionByUUIDRequest() {
        String line = "";
        String methodName = "";
        String uuid = "";
        Message toSend = new Message();

        try {
            while(true) {
                user.output("Is it an ask (1) or a bid (2) : ");
                line = user.input(); if(line.equals(QUIT)) return null;
                if(line.equals("1")) { methodName="getSellActionByUuid"; break; }
                if(line.equals("2")) { methodName="getBuyActionByUuid"; break; }
            }

            while(true) {
                user.output("Please provide ID : ");
                line = user.input(); if(line.equals(QUIT)) return null;
                if(!line.isEmpty()) { uuid = line; break; }
            }
        } catch (IOException e){ }

        return toSend.setHeaderField(MessageHeaderField.method, methodName).setBodyParam("uuid", uuid);
    }

    public static StockAction getStockActionFormCLI(){
        String line = "";
        StockAction stockAction = null;

        try {
            while(true) {
                user.output("Enter 1 (Ask), 2 (Bid), or end the session with 'quit': ");
                line = user.input(); if(line.equals(QUIT)) return null;
                if(line.equals("1"))
                {
                    stockAction = new StockAsk();
                    break;
                }
                else if(line.equals("2"))
                {
                    stockAction = new StockBid();
                    break;
                }
            }

            while(true) {
                user.output("Following a list of the companies featured : ");
                for (StockName name : StockName.values())
                {
                    user.output((name.ordinal()) + "." + name.toString() + " / ");
                }
                line = user.input(); if(line.equals(QUIT)) return null;
                if(line.matches("\\d") && (Integer.parseInt(line) < StockName.values().length))
                {
                    if((0 <= Integer.parseInt(line)) && (Integer.parseInt(line) < StockName.values().length))
                    {
                        int ordinal = Integer.parseInt(line);
                        stockAction.setStock(StockName.values()[ordinal]);
                        break;
                    }
                }
            }

            while(true) {
                user.output("Provide a price (float only) : ");
                line = user.input(); if(line.equals(QUIT)) return null;
                if(!line.isEmpty() && line.matches("^([+-]?\\d*\\.?\\d*)$"))
                {
                    double price = Double.parseDouble(line);
                    stockAction.setPrice(price);
                    break;
                }
            }
        } catch (IOException e){
            //TODO: handle exception
        }

        return stockAction;
    }
}
