package config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by gandy on 19.03.15.
 *
 */
public class Config {

    // return the MD5 hash from string msg
    private static String MD5(String msg){
        String digest = null;
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(msg.getBytes());

            for (byte b: hash ){
                sb.append(String.format("%02x", b&0xff));
            }
            digest = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest;
    }

    public static String getPasswordHash(String msg){
        String sol = "sdpaojf ;ldskjf;lkdsf j";
        return  MD5( MD5(msg + MD5(sol)) );
    }

    public static String getMacHash(String msg){
        String sol = "kvisyenbapbhyw/.z,x";
        return  MD5( MD5(msg + MD5(sol)) );
    }

   /* public static void main(String[] args) {
        System.out.println(getPasswordHash("gandy"));
    }*/

}
