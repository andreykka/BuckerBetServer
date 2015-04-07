package pojo;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by gandy on 24.09.14.
 *
 */

public class Customer {

    private static int ID = 0;

    private final SimpleIntegerProperty id;
    private SimpleStringProperty  name;
    private SimpleStringProperty  surname;
    private SimpleStringProperty  login;
    private SimpleStringProperty  tel;
    private SimpleStringProperty  email;
    private SimpleBooleanProperty status;

    public Customer(String name, String surname, String login,
                    String tel, String eMail, boolean status) {
        this.id = new SimpleIntegerProperty(++ID);
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.login = new SimpleStringProperty(login);
        this.tel = new SimpleStringProperty(tel);
        this.email = new SimpleStringProperty(eMail);
        this.status= new SimpleBooleanProperty(status);
    }


    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSurname() {
        return surname.get();
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getLogin() {
        return login.get();
    }

    public SimpleStringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getTel() {
        return tel.get();
    }

    public SimpleStringProperty telProperty() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel.set(tel);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public boolean getStatus() {
        return status.get();
    }

    public SimpleBooleanProperty statusProperty() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status.set(status);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;

        Customer customer = (Customer) o;

        if (email != null ? !getEmail().equals(customer.getEmail()) : customer.email != null) return false;
        if (login != null ? !getLogin().equals(customer.getLogin()) : customer.login != null) return false;
        if (name != null ? !getName().equals(customer.getName()) : customer.name != null) return false;
//        if (status != null ? !status.equals(customer.status) : customer.status != null) return false;
        if (surname != null ? !getSurname().equals(customer.getSurname()) : customer.surname != null) return false;
        if (tel != null ? !getTel().equals(customer.getTel()) : customer.tel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (tel != null ? tel.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
//        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}

