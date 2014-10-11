package test;

import database.Connector;
import pojo.FlagsEnum;
import pojo.OutputData;
import server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by gandy on 27.09.14.
 *
 */
public class Test {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        final int PORT = 15569;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started! ");

            Socket socket = serverSocket.accept();
            System.out.println("client accept");

            int r = socket.getInputStream().read();
            System.out.println("read " + r);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            int i=0;
            while (true){
                System.out.println("waiting data # " + i + " from client ");
                String line = in.readUTF();
                System.out.println("client send: " + line);
                if (line.equals("end"))
                    break;
                out.writeUTF(line);
                out.flush();
                System.out.println("send " + line + " to client");
                i++;
            }


        } catch (IOException e) {
            System.out.println("This PORT " + PORT +  " is unavailable \r\n"  + e.getMessage());
        }





/*
        // starting server
        Server server = Server.getInstance();

        // create connection
        Connector conn = Connector.getInstance();

        // get information from DB
        List<OutputData> outDataArr = new ArrayList<>(conn.getData());

        Timer timer = new Timer();

        System.out.println("timer starting");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("try send");
                server.sendBroadcastData(outDataArr);
                System.out.println("timer send data");
            }
        }, 10*1000, 10*1000);

        try {
            Thread.sleep(10*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timer.cancel();
        server.stopServer();
*/

    }
}
