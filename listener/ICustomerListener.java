package listener;

import pojo.Customer;

/**
 * Created by gandy on 19.03.15.
 *
 */
public interface ICustomerListener {

    public void customerLogin(Customer cust);
    public void customerLogout(Customer cust);
    public void customerRegister(Customer cust);

}
