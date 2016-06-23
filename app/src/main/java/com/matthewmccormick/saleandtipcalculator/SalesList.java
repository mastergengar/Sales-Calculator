package com.matthewmccormick.saleandtipcalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 11/16/2015.
 */
public class SalesList {
    private final double DAILY_SALES_MULTIPLIER = .92;
    private final double VME_MULTIPLIER = .064815;
    private final double MAX_SALE_TOTAL_FOR_VME = 77.14;

    private List<Sale> salesList = new ArrayList<Sale>();
    private double totalSales = 0;
    private double moneyOwed = 0;
    private double creditCardTips = 0;
    private boolean anAddedSaleCreatesBadVME = false;

    public void addSale(Sale sale){
        totalSales += sale.getSaleTotal();

        if (doesSaleCreateBadVMETotal(sale)) anAddedSaleCreatesBadVME = true;

        if (sale instanceof CreditSale){
            moneyOwed -= ((CreditSale) sale).getCreditTip();
            creditCardTips += ((CreditSale) sale).getCreditTip();
        }
        else if (sale instanceof CheckSale){
            moneyOwed += sale.getSaleTotal();
            moneyOwed -= ((CheckSale) sale).getCheckAmount();
        }
        else{
            moneyOwed += sale.getSaleTotal();
        }

        salesList.add(sale);
    }

    private boolean doesSaleCreateBadVMETotal(Sale sale){
        return sale.getSaleTotal() > MAX_SALE_TOTAL_FOR_VME;
    }

    public Sale getLastSaleAdded(){
        return salesList.get(salesList.size()-1);
    }

    public Sale removeLastAddedSale(){
        Sale removedSale = salesList.remove(salesList.size()-1);
        totalSales -= removedSale.getSaleTotal();

        if(removedSale instanceof CheckSale){
            moneyOwed -= removedSale.getSaleTotal();
            moneyOwed += ((CheckSale) removedSale).getCheckAmount();
        }
        else if (removedSale instanceof CreditSale){
            moneyOwed += ((CreditSale) removedSale).getCreditTip();
            creditCardTips -= ((CreditSale) removedSale).getCreditTip();
        }
        else{
            moneyOwed -= removedSale.getSaleTotal();
        }

        return removedSale;
    }

    public double getTotalSales(){
        return totalSales;
    }

    public double getMoneyOwed(){
        return moneyOwed;
    }

    public double getCreditCardTips(){
        return creditCardTips;
    }

    public List<Sale> getSalesList(){
        return salesList;
    }

    public double getDailySales(){
        return totalSales * DAILY_SALES_MULTIPLIER;
    }

    public double getDailyVME(){
        if (anAddedSaleCreatesBadVME){
            double vmeSaleTotal = 0;
            for (Sale sale: salesList){
                if (doesSaleCreateBadVMETotal(sale)) vmeSaleTotal += MAX_SALE_TOTAL_FOR_VME;
                else vmeSaleTotal += sale.getSaleTotal();
            }
            return vmeSaleTotal * VME_MULTIPLIER;
        }
        return totalSales * VME_MULTIPLIER;
    }

    public boolean isEmpty(){
        return salesList.isEmpty();
    }

    public String toString(){
        String toString = "";

        toString += "Total Sales: $" + String.format("%.2f", totalSales);
        toString += "; Money Owed: $" + String.format("%.2f", moneyOwed);
        toString += "; Credit Card Tips: $" + String.format("%.2f", creditCardTips);

        return toString;
    }
}
