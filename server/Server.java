package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandy on 23.09.14.
 *
 */
public class Server extends Thread{

    private ServerSocket    serverSocket;

//    private Boolean         serverWorking;

    private AcceptClient    acceptClient;

    public static Server getInstance(){
        return ServerInstanceHolder.INSTANCE;
    }

    private Server(){
        super("ServerThread");
//        this.serverWorking = true;
        this.serverSocket = null;
        this.start();
    }

    // send the content data to all connected Customers
    public void sendBroadcastData(){

      /*  for (Customer cust: this.customers){
            cust.sendData();
        } // for customers
*/
    }

    @Override
    public void run() {
            final int PORT = 65535;
            try {
                this.serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                System.out.println("This PORT " + PORT +  " is unavailable \r\n"  + e.getMessage());
                return; //!!!!!!
            }
        System.out.println("Server started! ");

        acceptClient = new AcceptClient(this.serverSocket);
    }

    public void stopServer(){

//        this.serverWorking = false;
        this.acceptClient.stopProcessing();
        this.interrupt();

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                System.out.println("Server does not stopped softly; \r\n try HARD STOP");
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server is stopped...");
    }

    private static class ServerInstanceHolder {

        private static final Server INSTANCE = new Server();

        private ServerInstanceHolder(){ }

    }

}
