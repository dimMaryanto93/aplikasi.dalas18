package dallastools.controllers.dataselections;

import java.util.Locale;

/**
 * Created by dimmaryanto on 14/12/15.
 */
public class LanguageSelector {
    private String lang;
    private Locale locale;

    public LanguageSelector() {
    }

    public LanguageSelector(String lang, Locale locale) {
        this.lang = lang;
        this.locale = locale;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return lang;
    }
}
