package dallastools.controllers.dataselections;

import dallastools.models.masterdata.Department;
import dallastools.models.masterdata.Employee;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 15/10/15.
 */
@Component
public class EmployeeChooser {

    public String getKey(Employee anEmployee) {
        return "(" + anEmployee.getId() + ") " + anEmployee.getEmployeeName();
    }

    public String getValue(Employee anEmployee) {
        Department job = anEmployee.getJobdesc();
        if (job != null) {
            return "(" + job.getName() + ") " + anEmployee.getEmployeeName();
        } else {
            return "(Null) " + anEmployee.getEmployeeName();
        }
    }
}
