import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.net.*;

/**
 *
 * @author Peter Altenberd
 * (Translated into English by Ronald Moore)
 * Computer Science Dept.                   Fachbereich Informatik
 * Darmstadt Univ. of Applied Sciences      Hochschule Darmstadt
 */

public class MainServer {
    public static void main(String[] args){
        Broker b = new Broker();
        Thread ss = new SocketServer(b);
        ss.start();
        Thread rpcServer = new XMLRPCServer(b);
        rpcServer.start();

        try {
            synchronized (ss) { ss.wait(); }
            synchronized (rpcServer) {rpcServer.wait(); }
            //rpcServer.wait();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
