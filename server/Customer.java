package server;

import java.net.Socket;

/**
 * Created by gandy on 24.09.14.
 *
 */

public class Customer extends Thread{

    private CustomerListener listener;

    private Socket socket;

    public Customer(Socket socket) {
        super("Customer Thread");
        this.socket = socket;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        this.listener = new CustomerListener(socket);
    }

    public boolean isActive(){
        return this.listener.getIsListenning();
    }

    public void stopListenning(){
        if ( this.listener != null && this.listener.getIsListenning()){
            this.listener.stopListen();
        }
        this.listener = null;
    }

    public void sendData(){
        this.listener.sendData();
    }

}
