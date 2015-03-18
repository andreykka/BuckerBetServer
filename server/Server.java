package server;


import database.Connector;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
/**
 * Created by gandy on 23.09.14.
 *
 */
public class Server extends Thread{

    private ServerSocket    serverSocket;

    private AcceptClient    acceptClient;

    private static final Logger LOGGER = Logger.getLogger(Server.class);

    public static Server getInstance(){
        return ServerInstanceHolder.INSTANCE;
    }

    private Server(){
        super("ServerThread");
        this.serverSocket = null;
        this.start();
    }

    // send the content data to all connected Customers
    @Override
    public void run() {
        final int PORT = 65535;
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            LOGGER.info("This PORT " + PORT +  " is unavailable \r\n"  + e.getMessage());
            JOptionPane.showMessageDialog(null, "Порт не доступен\r\nСервер не запущен. \r\nПопробуйте еще раз");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            this.stopServer();
            return;
        }
        LOGGER.info("Server started! ");
        acceptClient = new AcceptClient(this.serverSocket);
    }

    public void stopServer(){

//        this.serverWorking = false;
        this.acceptClient.stopProcessing();
        this.interrupt();
        Connector.getInstance().close();

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                LOGGER.info("Server does not stopped softly; try HARD STOP");
                this.serverSocket.close();
            } catch (IOException e) {
                LOGGER.error(e);
                //e.printStackTrace();
            }
        }

        LOGGER.info("Server is stopped...");
    }
/*
    public void sendBroadcastData(){
        List<Customer> list = this.acceptClient.getConnectedCustomers();
        for (Customer cust: list) {
            cust.sendData();
        }
    }*/

    private static class ServerInstanceHolder {

        private static final Server INSTANCE = new Server();

        private ServerInstanceHolder(){ }

    }

}
