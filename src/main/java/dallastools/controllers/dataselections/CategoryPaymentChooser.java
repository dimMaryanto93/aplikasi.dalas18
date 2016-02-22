package dallastools.controllers.dataselections;

import dallastools.models.masterdata.CategoryOfPayment;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 18/10/15.
 */
@Component
public class CategoryPaymentChooser {

    public String getKey(CategoryOfPayment category) {
        return "(" + category.getId() + ") " + category.getPaymentFor();
    }
}
