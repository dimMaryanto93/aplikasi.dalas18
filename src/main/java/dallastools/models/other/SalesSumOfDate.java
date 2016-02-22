package dallastools.models.other;

import java.time.LocalDate;

/**
 * Created by dimmaryanto on 10/11/15.
 */
public class SalesSumOfDate {
    private LocalDate date;
    private Double grantTotal;
    private Double ammount;
    private Double salesRecivable;
    private Double income;

    public SalesSumOfDate(LocalDate date, Double grantTotal, Double ammount) {
        this.date = date;
        this.grantTotal = grantTotal;
        this.ammount = ammount;
        this.salesRecivable = grantTotal - ammount;
        this.income = grantTotal - salesRecivable;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getGrantTotal() {
        return grantTotal;
    }

    public void setGrantTotal(Double grantTotal) {
        this.grantTotal = grantTotal;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getSalesRecivable() {
        return salesRecivable;
    }

    public void setSalesRecivable(Double salesRecivable) {
        this.salesRecivable = salesRecivable;
    }
}
