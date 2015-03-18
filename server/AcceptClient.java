package server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by gandy on 27.09.14.
 *
 * */


class AcceptClient extends Thread {

    private volatile    ServerSocket        serverSocket;

    private volatile    ArrayList<Customer> customers;

    private             boolean             proccessing;
    private static final Logger LOGGER = Logger.getLogger(AcceptClient.class);
    
    public AcceptClient(ServerSocket serverSocket) {
        super("AcceptClientThread");
        this.serverSocket   = serverSocket;
        this.customers      = new ArrayList<>();
        this.proccessing    = true;
        this.start();
    }

    // stop listeneng, and delete disconect Customers
    private void checkCustomersList(){
        //System.out.println("customersSize: " + customers.size());
        if (this.customers == null || this.customers.isEmpty()){
            //System.out.println("customers is empty");
            return;
        }
        for(int i=0; i < customers.size(); i++) {
            if ( !customers.get(i).isActive()) {
                customers.get(i).logOut();
                customers.remove(i);
                //System.out.println("Customer is not active.removed");
                customers.trimToSize();
            }
        }
    }

 /*   public List<Customer> getConnectedCustomers(){
        this.checkCustomersList();
        return this.customers;
    }
*/
    public void stopProcessing(){
        this.proccessing = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (proccessing) try {
            LOGGER.info("accepting client ...");
            //System.out.println("accepting client");
            if (serverSocket == null)
                LOGGER.info("serverSocket = null");
             //System.out.println("serverSocket = null");

            Socket clientSocket;
            if (serverSocket.isClosed()){
                this.stopProcessing();
                break;
            }
            clientSocket = serverSocket.accept();
            LOGGER.info("client accepted");
            //System.out.println("client accepted");

            if (clientSocket != null){
                LOGGER.info("try add new client");
                //System.out.println("try add new client");
                this.checkCustomersList();
                customers.add(new Customer(clientSocket));
                //System.out.println("customer sizze: " + customers.size());
                LOGGER.info("add new client");
                //System.out.println("added new client");
                //System.out.println("accept new client, now are: " + customers.size() + " clients");
                LOGGER.info("accept new client, now are: " + customers.size() + " clients");

            }
             //System.out.println("client is null!!!  in AcceptClient");
             LOGGER.info("client is null!!!  in AcceptClient");


        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

}
