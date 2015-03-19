package server;

import org.apache.log4j.Logger;
import pojo.LogInData;

import java.net.Socket;

/**
 * Created by gandy on 24.09.14.
 *
 */

public class Customer extends Thread{

    private static final    Logger      LOGGER  = Logger.getLogger(Customer.class);
    public  static          LogInData   user    = new LogInData("not logged ", "", "");

    private volatile CustomerListener listener;

    private Socket socket;

    public Customer(Socket socket) {
        super("customer thread");
        this.socket = socket;
        start();
    }

    public LogInData getInfo(){
        if (user == null){
           return  new LogInData("not logged ", "", "");
        } else
        return  user;
    }

    @Override
    public void run() {
        super.run();
        this.listener = new CustomerListener(socket);
    }

    public boolean isActive(){
        return this.listener.getIsListening();
    }

    public void logOut(){
        this.listener.logOut();
        this.listener = null;
    }

    public void sendData(){
        if (listener == null) {
            System.out.println("listener == null");
            LOGGER.error("listener == null");
        }
        this.listener.sendData();
    }

}
