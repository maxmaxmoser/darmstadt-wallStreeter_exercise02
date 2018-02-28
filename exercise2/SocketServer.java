import java.net.*;


public class SocketServer extends Thread{
    private Broker b;

    public SocketServer(Broker b) {
        this.b = b;
    }

    @Override
    public void run (){
        try{
            int port = 9999;
            ServerSocket listenSocket = new ServerSocket(port);
            System.out.println("Multithreaded Server starts on Port "+port);
            while (true){
                Socket client = listenSocket.accept();
                System.out.println("Connection with: " +     // Output connection
                        client.getRemoteSocketAddress());   // (Client) address
                new StockActionService(client, b).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}