package config;

import exceptions.TaskNotExecuteCorrectException;

/**
 * Created by gandy on 08.04.15.
 *
 */
@FunctionalInterface
public interface Task {

    public void execute() throws TaskNotExecuteCorrectException;

}