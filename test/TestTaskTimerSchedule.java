package test;

import config.Task;
import config.TaskTimerScheduler;

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
    }

}
