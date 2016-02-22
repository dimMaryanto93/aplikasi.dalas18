package dallastools.models.other;

/**
 * Created by dimMaryanto on 1/15/2016.
 */
public enum Level {
    ADMIM("ADMIN"),
    BENDAHARA("BENDAHARA"),
    PRODUKSI("PRODUKSI"),
    PEMILIK("PEMILIK");

    private String value;

    Level(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
