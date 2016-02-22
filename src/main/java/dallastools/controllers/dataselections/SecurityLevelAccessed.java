package dallastools.controllers.dataselections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 11/10/15.
 */
@Component
public class SecurityLevelAccessed {

    private ObservableList<String> list;

    public SecurityLevelAccessed() {
        list = FXCollections.observableArrayList();
        list.add("ADMIN");
        list.add("BENDAHARA");
        list.add("PRODUKSI");
        list.add("PEMILIK");
    }

    public ObservableList<String> getList() {
        return list;
    }

}
