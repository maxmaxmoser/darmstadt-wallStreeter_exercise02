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
