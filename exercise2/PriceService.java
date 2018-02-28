import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class PriceService {
    static String line = "";
    static UserInterface user = new UserInterface();
    static Socket socket;
    static BufferedReader fromServer;
    static DataOutputStream toServer;
    static Trader trader;
    final static String QUIT = "quit";

    public static void main(String[] args) throws Exception {
        String line = "";
        String dateBefore = "";
        String dateAfter = "";
        String stockSelected = "";
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        try {
            while(true) {
                user.output("What do you want to do ? (1) Ask price per name or (2) Ask data per stock name per price within a range of dates : ");
                line = user.input();
                if(line.equals(QUIT)) break;
                if(line.equals("1")){
                    if(line != null && !line.isEmpty()){
                        while(true) {
                            user.output("Following a list of the companies featured. Please enter the number of the stock name you want to search : ");
                            for (StockName name : StockName.values())
                            {
                                user.output((name.ordinal()) + "." + name.toString() + " / ");
                            }
                            line = user.input();
                            if(line.equals(QUIT)) break;
                            if(line.matches("\\d") && (Integer.parseInt(line) < StockName.values().length))
                            {
                                if((0 <= Integer.parseInt(line)) && (Integer.parseInt(line) < StockName.values().length))
                                {
                                    int ordinal = Integer.parseInt(line);
                                    stockSelected = StockName.values()[ordinal].name();
                                    System.out.println("StockeSlected : " + stockSelected);
                                    Object[] params = new Object[]{new String("25/02/2018"), new String("25/03/2018"), new String(stockSelected)};

                                    String result =  (String) client.execute("Broker.searchAction", params);
                                    System.out.println("Liste des transactions de " + params[2] + ":\n" + result );
                                    break;
                                }
                            }
                        }
                    }
                }
                if(line.equals("2")){
                    if(line != null && !line.isEmpty()){
                        while(true) {
                            user.output("Following a list of the companies featured. Please enter the number of the stock name you want to search : ");
                            for (StockName name : StockName.values())
                            {
                                user.output((name.ordinal()) + "." + name.toString() + " / ");
                            }
                            line = user.input();
                            if(line.equals(QUIT)) break;
                            if(line.matches("\\d") && (Integer.parseInt(line) < StockName.values().length))
                            {
                                if((0 <= Integer.parseInt(line)) && (Integer.parseInt(line) < StockName.values().length))
                                {
                                    int ordinal = Integer.parseInt(line);
                                    stockSelected = StockName.values()[ordinal].name();
                                    System.out.println("StockeSlected : " + stockSelected);
                                    user.output("Please enter the date range you want to search . ex : dateAfter:dateBefore in the following format dd/mm/yyyy:dd/mm/yyyy : ");
                                    line = user.input();
                                    if(line.equals(QUIT)) break;
                                    if(line != null && !line.isEmpty()){
                                        dateAfter = line.split(":")[0];
                                        dateBefore = line.split(":")[1];
                                        System.out.println("dateAfter : " + dateAfter + "\n" + " dateBefore : " + dateBefore);
                                        Object[] params = new Object[]{new String(dateAfter), new String(dateBefore), new String(stockSelected)};

                                        String result =  (String) client.execute("Broker.searchAction", params);
                                        System.out.println("Liste des transactions de " + params[2] + ":\n" + result );
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e){
            System.out.println("Exception : " + e);
        }



    }



}
