package listenner;

import pojo.Customer;
import server.CustomerService;

/**
 * Created by gandy on 26.03.15.
 *
 */
public interface ICustomerServiceListener {

    public void customerConnected(CustomerService customerService);
    public void customerDisconnect(CustomerService customerService);

}
