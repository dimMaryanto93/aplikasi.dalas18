package dallastools.controllers.dataselections;

import dallastools.models.masterdata.Department;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 30/10/15.
 */
@Component
public class DepartmentChooser {
    public String getKey(Department aDepartment) {
        return "(" + aDepartment.getId() + ") " + aDepartment.getName();
    }
}
