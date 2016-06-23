package com.matthewmccormick.saleandtipcalculator;

import android.support.v7.app.ActionBarActivity;

import java.util.List;

/**
 * Created by Matthew on 11/26/2015.
 */
public class SalesListSharedPreferenceAdaptor extends ActionBarActivity {
    private static final String CASH_TOKEN = "C";
    private static final String CHECK_TOKEN = "H";
    private static final String CREDIT_TOKEN = "R";
    private static final String DETAIL_DELIMITER = ",";
    private static final String SALE_DELIMITER = ":";

    public static String buildPreferenceString(List<Sale> salesList){
        String preferenceString = "";

        for (Sale sale: salesList){
            preferenceString += sale.getSaleTotal() + DETAIL_DELIMITER;

            if (sale instanceof CreditSale){
                preferenceString += CREDIT_TOKEN + DETAIL_DELIMITER + ((CreditSale) sale).getCreditTip();
            }
            else if (sale instanceof CheckSale){
                preferenceString += CHECK_TOKEN + DETAIL_DELIMITER  + ((CheckSale) sale).getCheckAmount();
            }
            else{
                preferenceString += CASH_TOKEN;
            }

            preferenceString += SALE_DELIMITER;
        }

        return preferenceString;
    }

    public static SalesList buildSalesList(String jsonString){


        SalesList salesList = new SalesList();

        for (String saleString: jsonString.split(SALE_DELIMITER)){
            if (!saleString.isEmpty()){
                String[] saleDetails = saleString.split(DETAIL_DELIMITER);

                double saleTotal = Double.parseDouble(saleDetails[0]);

                if (saleDetails[1].equals(CASH_TOKEN)){
                    salesList.addSale(new Sale(saleTotal));
                }
                else{
                    double saleTotalReceived = Double.parseDouble(saleDetails[2]);

                    if (saleDetails[1].equals(CHECK_TOKEN)){
                        salesList.addSale(new CheckSale(saleTotal, saleTotalReceived));
                    }
                    else if (saleDetails[1].equals(CREDIT_TOKEN)){
                        salesList.addSale(new CreditSale(saleTotal, saleTotalReceived));
                    }
                }
            }
        }

        return salesList;
    }
}
