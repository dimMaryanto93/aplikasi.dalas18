package dallastools.controllers.dataselections;

import dallastools.models.masterdata.Customer;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 14/10/15.
 */
@Component
public class CustomerChooser {

    public String getKey(Customer aCustomer) {
        return "(" + aCustomer.getPhone() + ") " + aCustomer.getCustomerName();
    }
}
