package config;

import exceptions.TaskNotExecuteCorrectException;

import java.util.concurrent.TimeUnit;

/**
 * Created by gandy on 08.04.15.
 *
 */

public class TaskTimerScheduler {

    private boolean isComplete; // flag indicate status execution task
    private int timeMiliseconds; // count miliseconds on executing
    private Task task;

    /**
     *
     * Result   true    if function executed over time
     *          false   if not execute.
     * @param task function that will executing
     * @param timeMiliseconds the range of time on execute
     *
     */
    public TaskTimerScheduler(Task task, int timeMiliseconds){
        isComplete = false;
        this.task = task;
        this.timeMiliseconds = timeMiliseconds;
    }

    public boolean executeTask () {
        Thread t = new Thread(() -> {
//            System.out.println("before execute");
            try {
                task.execute();
            } catch (TaskNotExecuteCorrectException e) {
                isComplete = false;
                return;
            }
//            System.out.println("task executed");
            isComplete = true;
        });
        t.setDaemon(true); // thread interrupt if Parent interrupted
        t.start();

        try {
            TimeUnit.MILLISECONDS.sleep(this.timeMiliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("interrupt task");
        t.interrupt();

//        System.out.println("isInterrupted " + t.isInterrupted());
        return isComplete;
    }

}