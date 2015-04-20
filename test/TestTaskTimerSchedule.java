package test;

import config.Task;
import config.TaskTimerScheduler;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import java.util.regex.Pattern;

/**
 * Created by gandy on 08.04.15.
 *
 */

public class TestTaskTimerSchedule{

    public static void main(String[] args) {
        System.out.println("Test::start executing task");
        TaskTimerScheduler tts = new TaskTimerScheduler(new Task() {
            @Override
            public void execute() {
                int k = 0;
                for(long i = 1; i < Long.MAX_VALUE -2; i++){
                    if (i % 100_000_000 == 0)
                        System.out.println(i);
                    k = (int) i;
                }
                System.out.println("Test::Task Executed");
            }
        }, 2000);

        System.out.println("Test::Execution return: " + tts.executeTask());
/*
        Pattern p = Pattern.compile("//d++");
        String daysCount;
        String msg = "Введите количество дней для активации";
        boolean f = false;
        do {
            daysCount = JOptionPane.showInputDialog(null, msg, Integer.valueOf(7));
            if (daysCount == null)
                break;
            if (!f)
                msg = msg + "\r\nНужно вводить только цыфры";
            f = true;
        } while (!p.matcher(daysCount).matches());

        if (daysCount == null) {
            JOptionPane.showMessageDialog(null, "Активация не удалась...");
            return;
        }
        int val = Integer.parseInt(daysCount);
        System.out.println("daysCount = " + val);*/

    }

}
