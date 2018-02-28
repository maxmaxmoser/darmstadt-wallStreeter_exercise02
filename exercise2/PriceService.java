import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class PriceService {
    public static void main(String[] args) throws Exception {

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();


        config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        String test = "APPL";
        Object[] params = new Object[]{new String("25/02/2018"), new String("25/03/2018"), new String(test)};

        String result =  (String) client.execute("Broker.action", params);
        System.out.println("Liste des transactions de " + params[2] + ":\n" + result );
    }
}
