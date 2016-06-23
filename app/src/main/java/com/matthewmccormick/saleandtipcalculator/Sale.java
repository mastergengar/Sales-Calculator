package com.matthewmccormick.saleandtipcalculator;

/**
 * Created by Matthew on 7/29/2015.
 */
public class Sale {
    private double saleTotal;

    public Sale (double saleTotal){
        this.saleTotal = saleTotal;
    }

    public String getSaleType(){
        return "Cash";
    }

    public double getSaleTotal(){
        return saleTotal;
    }

    public String toString(){
        return "Sale Total: $" + String.format("%.2f", saleTotal);
    }
}