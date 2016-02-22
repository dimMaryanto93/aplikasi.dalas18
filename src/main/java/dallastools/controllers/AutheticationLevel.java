package dallastools.controllers;

import dallastools.models.other.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by dimMaryanto on 1/15/2016.
 */
public class AutheticationLevel {

    private static ObservableList<Level> levels = FXCollections.observableArrayList();

    static {
        levels.add(Level.BENDAHARA);
        levels.add(Level.PEMILIK);
        levels.add(Level.PRODUKSI);
    }

    public static ObservableList<Level> getLevels() {
        return levels;
    }

    public static String getValue(Integer index) {
        return levels.get(index).getValue();
    }

    public static String getValue(Level level) {
        return level.getValue();
    }
}
