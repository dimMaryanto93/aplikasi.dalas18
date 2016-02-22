package dallastools.controllers.dataselections;

import dallastools.models.masterdata.Warehouse;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 22/10/15.
 */
@Component
public class WarehouseChooser {

    public String getKey(Warehouse aWarehouse) {
        return "(" + aWarehouse.getId() + ") " + aWarehouse.getName();
    }
}
