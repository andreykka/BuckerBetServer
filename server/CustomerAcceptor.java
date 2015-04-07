package server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gandy on 27.09.14.
 *
 * */

class CustomerAcceptor implements Runnable {

    private class LocalExecutorCustomerAcceptor implements Runnable {

        // stop listening, and delete disconnected Customers
        private void checkCustomersList(){
            if (customerServices == null || customerServices.isEmpty()){
                //System.out.println("customers is empty");
                return;
            }
            for(int i = 0; i < customerServices.size(); i++) {
                CustomerService current = customerServices.get(i);
                if (!current.checkConnection()) {
                    current.logOut();
                    customerServices.remove(i);
                    customerServices.trimToSize();
                }
            }
        }

        @Override
        public void run() {
            while (processing) try {
                LOGGER.info("accepting client ...");
                if (serverSocket == null || serverSocket.isClosed()) {
                    LOGGER.info("serverSocket = null or closed");
                    stopProcessing();
                    break;
                }

                Socket clientSocket;
                Socket connectionSocket;

                // the thread in this place sleep, and still wait for new customer
                // connect the socket for transport data
                clientSocket = serverSocket.accept();
                LOGGER.info("accept clientSocket");
                // connect socket for checking connection, and broadcast sending
                connectionSocket = serverSocket.accept();

                LOGGER.info("accept connectionSocket");

                if (clientSocket == null || connectionSocket == null) {
                    LOGGER.info("client is null!!!  in AcceptClient");
                    continue;
                }
                LOGGER.info("try add new client");

                checkCustomersList();
                CustomerService customerService = new CustomerService(clientSocket, connectionSocket);
                customerServices.add(customerService);
                // alert all listeners about customer connect

                // start the customer service listener
                executorService.submit(customerService);

                LOGGER.info("add new client");
            } catch (IOException e) {
                LOGGER.error(e);
//                stopProcessing();
            }
        }
    }

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private volatile    ServerSocket    serverSocket;
    private ArrayList<CustomerService>  customerServices;
    private volatile    boolean         processing;
    private LocalExecutorCustomerAcceptor executorCustomerAcceptor = new LocalExecutorCustomerAcceptor();
    private Thread thread;

    private static final Logger         LOGGER = Logger.getLogger(CustomerAcceptor.class);

    public CustomerAcceptor(ServerSocket serverSocket) {
        this.serverSocket       = serverSocket;
        this.processing         = true;
        this.customerServices   = new ArrayList<>();
        this.thread             = new Thread(executorCustomerAcceptor);
    }

    // may produce problem with memory
    public List<CustomerService> getActiveCustomers(){
        return this.customerServices;
    }

    public void stopProcessing(){
        // stop all the Customer Services
        executorService.shutdown();
        executorService.shutdownNow();
        executorCustomerAcceptor.checkCustomersList();

        customerServices.forEach(server.CustomerService::logOut);

        if (processing)
            processing = false;
        if (thread != null)
            thread.interrupt();
    }

    @Override
    public void run() {
        thread.start();
    }

}
