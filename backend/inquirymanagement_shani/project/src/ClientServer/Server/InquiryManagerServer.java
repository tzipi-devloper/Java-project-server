package ClientServer.Server;

import Business.InquiryManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class InquiryManagerServer {
    ServerSocket myServer;
    boolean isRunning;
    public InquiryManagerServer( int port) {
        try {
            myServer = new ServerSocket(port);
            System.out.println("Server initialized on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InquiryManager InquiryManager=new InquiryManager();
    }

    public void start() {
        isRunning = true;
        System.out.println("Server is waiting for clients...");

            new Thread(() -> {
        while (isRunning) {
            try {
                Socket clientSocket = myServer.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                HandleClient handleClient = new HandleClient(clientSocket);
                Thread clientThread = new Thread(handleClient);
                clientThread.start();

            } catch (IOException e) {
                if (isRunning) {
                    e.printStackTrace();
                }
            }
        }
    }).start();
    }

    public void stop() {
        isRunning = false;
        try {
            if (myServer != null && !myServer.isClosed()) {
                myServer.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
