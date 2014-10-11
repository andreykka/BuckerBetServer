package pojo;

import java.io.Serializable;

/**
 * Created by gandy on 27.09.14.
 */
public class LogInData implements Serializable {

    private String login;
    private String pass;

    public LogInData (LogInData obj){
        this.setLogin(obj.getLogin());
        this.setPass(obj.getPass());
    }

    public LogInData() {

    }

    public LogInData(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
