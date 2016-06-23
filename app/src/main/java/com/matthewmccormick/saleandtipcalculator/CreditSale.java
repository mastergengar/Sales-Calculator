package com.matthewmccormick.saleandtipcalculator;

/**
 * Created by Matthew on 11/16/2015.
 */
public class CreditSale extends Sale {
    private double creditTip;

    public CreditSale(double sale, double tip){
        super(sale);

        creditTip = tip;
    }

    public String getSaleType(){
        return "Credit";
    }

    public double getCreditTip(){
        return creditTip;
    }

    public String toString() {
        String toString = "Credit Card " + super.toString();
        toString += "; Credit Tip: $" + String.format("%.2f", creditTip);
        return toString;
    }
}
