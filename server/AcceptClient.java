package server;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandy on 27.09.14.
 *
 * */


class AcceptClient extends Thread {

    private volatile    ServerSocket    serverSocket;

    private             ArrayList<Customer>  customers;

    private             boolean         proccessing;

    public AcceptClient(ServerSocket serverSocket) {
        super("AcceptClientThread");
        this.serverSocket   = serverSocket;
        this.customers      = new ArrayList<>();
        this.proccessing    = true;
        this.start();
    }

    // stop listeneng, and delete disconect Customers
    private void checkCustomersList(){
        /*if (!this.customers.isEmpty()) {
            this.customers.stream() .filter(cust -> !cust.isActive())
                                    .forEach(Customer::stopListenning);
            }*/
            System.out.println("customersSize: " + customers.size());
            if (this.customers.isEmpty()){
                System.out.println("customers is empty");
                return;
            }
            for(int i=0; i < customers.size(); i++) {
                if ( !customers.get(i).isActive()) {
                    customers.remove(i);
                    System.out.println("removed");
                    customers.trimToSize();
                }
            }
    }

    public List<Customer> getConnectedCustomers(){
        this.checkCustomersList();
        return this.customers;
    }

    public void stopProcessing(){
        this.proccessing = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (proccessing) try {
            System.out.println("accepting client");
            if (serverSocket == null) System.out.println("serverSocket = null");

            Socket clientSocket;
            if (serverSocket.isClosed()){
                this.stopProcessing();
                break;
            }
            clientSocket = serverSocket.accept();
            System.out.println("client accepted");

            if (clientSocket != null){
                System.out.println("try add new client");
                this.checkCustomersList();
                customers.add(new Customer(clientSocket));
                System.out.println("added new client");
            } else System.out.println("client is null!!!  in AcceptClient");

            System.out.println("accept new client, now are: " + customers.size() + " clients");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
