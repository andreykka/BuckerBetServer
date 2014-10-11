package log;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by gandy on 11.10.14.
 *
 */
public class Log {

    private static FileWriter writer;

    static {
        try {
            writer = new FileWriter("server.log");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "error creating log file");
        }
    }

    public static void write(String msg){
        try {
            writer.write(msg + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Не возможно записать в файл");
        }
    }

    public static void write(Exception e){
        write("---------------------------------------------------------------------");
        write("Message: ");
        write(e.getMessage());
        write("Stack trace: ");
        for (StackTraceElement el : e.getStackTrace()) {
            write("\tClass:\t " + el.getClassName());
            write("\tLine:\t " + el.getLineNumber());
            write("\tMethod:\t " + el.getMethodName());
        }
        write("---------------------------------------------------------------------");

    }


}
