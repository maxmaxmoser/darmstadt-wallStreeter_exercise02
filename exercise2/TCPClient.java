/**
 *
 * @author Peter Altenberd
 * (Translated into English by Ronald Moore)
 * Computer Science Dept.                   Fachbereich Informatik
 * Darmstadt Univ. of Applied Sciences      Hochschule Darmstadt
 */

import java.io.*;
import java.net.*;
import java.util.UUID;

public class TCPClient {
    static String line = "";
    static UserInterface user = new UserInterface();
    static Socket socket;
    static BufferedReader fromServer;
    static DataOutputStream toServer;
    static Trader trader;

    public static void main(String[] args) throws Exception {
        user.output("Please provide IP:PORT of the server, or hit enter to leave the defaults : ");
        line = user.input();
        String ip = "localhost";
        String port = "9999";
        String lineParts[] = line.split(":");

        if(lineParts.length == 2) {
            ip = lineParts[0];
            port =  lineParts[1];
        }

        socket = new Socket(ip, Integer.parseInt(port));
        toServer = new DataOutputStream(socket.getOutputStream());
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        while(true) {
            Message toSend = Trader.getRequest();
            if(toSend == null) break;
            sendRequest(toSend);
            receiveResponse();
        }

        socket.close();
        toServer.close();
        fromServer.close();
    }

    private static void sendRequest(Message request) throws IOException {
        user.output("Sending request : \n" + request.toString());
        toServer.writeBytes(request.getEncapsulated());
    }

    private static void receiveResponse() throws IOException {
        StringBuilder response = new StringBuilder();
        String firstLine = fromServer.readLine();
        int contentLength = Integer.parseInt(firstLine);
        for(int i = 0; i<contentLength; i++){
            response.append((char)fromServer.read());
        }

        Message received = new Message(response.toString());
        user.output("\nServer answers: \n--------------");
        user.output(received.toString() + "\n --------------\n");
    }
}
