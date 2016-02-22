package dallastools.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;

/**
 * Created by dimMaryanto on 1/15/2016.
 */
@Component
public class NumberFormatter {

    private NumberFormat currency;
    private NumberFormat number;

    @Autowired
    public NumberFormatter(
            @Qualifier("currencyInstance") NumberFormat currency,
            @Qualifier("numberInstance") NumberFormat number) {
        this.currency = currency;
        this.number = number;
    }

    public String getCurrency(Number value) {
        return currency.format(value);
    }

    public String getNumber(Number value) {
        return number.format(value);
    }


}
