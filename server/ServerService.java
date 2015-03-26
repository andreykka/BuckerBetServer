package server;

import database.Connector;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by gandy on 19.03.15.
 *
 */

public class ServerService {

    private static ServerService ourInstance = new ServerService();
    public static ServerService getInstance() {
        return ourInstance;
    }

    private Logger logger = Logger.getLogger(getClass());

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile Server server;
    private volatile CustomerAcceptor acceptor;
    private volatile ArrayList<Customer> customers; //  need synchronize

    private ServerService() {

    }

    public void runServer(){
        server = Server.getInstance();
        Future<?> serverFuture = executorService.submit(server);
        try {
            // wait before turn on the server
            serverFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // run accepting client
        acceptor = new CustomerAcceptor(server.getServerSocket());
        executorService.submit(acceptor);

    }

    public void stopServer(){
        // stop connection with database
        Connector.getInstance().close();

        // shutdown all Threads if possible
        executorService.shutdown();

        // stop acception clients
        acceptor.stopProcessing();

        // shut down the server
        server.stopServer();

    }

}
