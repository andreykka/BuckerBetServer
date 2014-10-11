package pojo;

import java.io.Serializable;

/**
 * Created by gandy on 27.09.14.
 */
public class RegistrationData  implements Serializable{

    private String name;
    private String surname;
    private String login;
    private String password;
    private String eMail;

    public RegistrationData() {
    }

    public RegistrationData(RegistrationData regData) {
        this.name       = regData.getName();
        this.surname    = regData.getSurname();
        this.login      = regData.getLogin();
        this.password   = regData.getPassword();
        this.eMail      = regData.getEMail();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }
}
