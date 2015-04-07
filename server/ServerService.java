package server;

import database.Connector;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by gandy on 19.03.15.
 *
 */

public class ServerService {

    private static ServerService instance = new ServerService();
    public static  ServerService getInstance() {
        return instance;
    }
    private ServerService() { }
    private Logger logger = Logger.getLogger(getClass());

    private ExecutorService             executorService = Executors.newCachedThreadPool();

    private volatile Server             server;
    private volatile CustomerAcceptor   acceptor;

//    private volatile ArrayList<LogInData> customers; //  need synchronize

    public void runServer(){
        server = Server.getInstance();
        Future<?> serverFuture = executorService.submit(server);
        try {
            // wait before turn on the server
            serverFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("server started !!!!!!");
        // run accepting client
        acceptor = new CustomerAcceptor(server.getServerSocket());
        executorService.submit(acceptor);

        // shutdown all Threads if possible
        executorService.shutdown();
    }

    public void stopServer(){
        // stop accepting clients
        acceptor.stopProcessing();

        // shut down the server
        server.stopServer();

        // close connection with database
        Connector.getInstance().close();
    }

    // possible lost of performance
    public void sendBroadcastData() {
        List<CustomerService> activeCustomers = this.acceptor.getActiveCustomers();
        synchronized (activeCustomers) {
            for (CustomerService cs : activeCustomers)
                cs.sendBroadcastData();
        }
    }


}
