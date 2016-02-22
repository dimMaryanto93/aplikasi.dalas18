package dallastools.controllers.dataselections;

import dallastools.models.masterdata.Suplayer;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 17/10/15.
 */
@Component
public class SuplayerChooser {

    public String getKey(Suplayer aSuplayer) {
        return "(" + aSuplayer.getSuplayerId() + ") " + aSuplayer.getName();
    }
}
