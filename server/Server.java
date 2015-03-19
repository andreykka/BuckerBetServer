package server;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by gandy on 23.09.14.
 *
 */

public class Server implements Runnable{

    private static final    Logger          LOGGER = Logger.getLogger(Server.class);
    private volatile        ServerSocket    serverSocket;

    public static Server getInstance(){
        return ServerInstanceHolder.INSTANCE;
    }

    private Server(){
        this.serverSocket = null;
    }

    @Override
    public void run() {
        final int PORT = 65535;
        try {
            this.serverSocket = new ServerSocket(PORT);
            LOGGER.info("Server started! ");
        } catch (IOException e) {
            LOGGER.info("This PORT " + PORT + " is unavailable \r\n" + e.getMessage());
            JOptionPane.showMessageDialog(null, "Порт не доступен\r\nСервер не запущен. \r\nПопробуйте еще раз");
            this.stopServer();
        }
    }

    public void stopServer(){
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                this.serverSocket.close();
                LOGGER.info("Server shutdown successfully");
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        LOGGER.info("Server is stopped...");
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private static class ServerInstanceHolder {

        private static final Server INSTANCE = new Server();

        private ServerInstanceHolder(){ }

    }

}
