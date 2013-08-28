package cl.rvillablanca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket server = new Socket("localhost", 4444);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String message = null;
        while ((message = br.readLine()) != null) {
            if (message.toLowerCase().equals("bye")) {
                break;
            }
            server.getOutputStream().write(message.getBytes());
        }
        server.close();
    }
}
