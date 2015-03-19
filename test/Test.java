package test;

import database.Connector;

import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * Created by gandy on 27.09.14.
 *
 */
public class Test {

    public static void main(String[] args) throws SocketException {

        String mac = getMac();
        System.out.print(mac);

    }


    public static String getMac(){
        StringBuilder result = new StringBuilder();
        Enumeration<NetworkInterface> en = null;
        try {
            en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()){
                NetworkInterface in = en.nextElement();
                byte[] mac = in.getHardwareAddress();
                if (mac != null) {
                    for (byte aMac : mac) {
                        result.append(aMac);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }



        return result.toString();
    }



}
