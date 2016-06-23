package com.matthewmccormick.saleandtipcalculator;

/**
 * Created by Matthew on 11/16/2015.
 */
public class CheckSale extends Sale{
    private double checkAmount;

    public CheckSale(double sale, double check){
        super(sale);
        checkAmount = check;
    }

    public String getSaleType(){
        return "Check";
    }

    public double getCheckAmount(){
        return checkAmount;
    }

    public String toString() {
        String toString = "Check " + super.toString();
        toString += "; Check Amount: $" + String.format("%.2f", checkAmount);
        return toString;
    }
}
