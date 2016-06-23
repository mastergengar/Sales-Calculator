package com.matthewmccormick.saleandtipcalculator;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Matthew on 2/5/2016.
 */
public class AllSalesScreen extends ActionBarActivity {

    SalesList salesList = new SalesList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sales_screen);

        retrieveOrCreateSalesList();
        setSaleValueStrings();
        fillSalesTable();
    }

    private void retrieveOrCreateSalesList(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String preferenceString = sharedPreferences.getString("ADAPTOR_SALES_LIST", "");
        salesList = SalesListSharedPreferenceAdaptor.buildSalesList(preferenceString);
    }

    private void setSaleValueStrings(){
        ((TextView) findViewById(R.id.totalSalesTextView)).setText("$" + String.format("%.2f", salesList.getTotalSales()));
        ((TextView) findViewById(R.id.moneyOwedTextView)).setText("$" + String.format("%.2f", salesList.getMoneyOwed()));
        ((TextView) findViewById(R.id.creditTipsTextView)).setText("$" + String.format("%.2f", salesList.getCreditCardTips()));
    }

    private void fillSalesTable(){
        TableLayout salesTable = (TableLayout) findViewById(R.id.allSalesTableLayout);
        List<Sale> saleList = salesList.getSalesList();

        for (int i = 0; i <saleList.size(); i++) {
            Sale sale = saleList.get(i);

            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
            row.setLayoutParams(lp);
            row.setPadding(0,0,0,5);

            TextView saleNumber = new TextView(this);
            saleNumber.setGravity(Gravity.CENTER);
            saleNumber.setTextSize(16);
            saleNumber.setText("" + (i+1));
            row.addView(saleNumber);

            TextView saleType = new TextView(this);
            saleType.setGravity(Gravity.CENTER);
            saleType.setTextSize(16);
            saleType.setText(sale.getSaleType());
            row.addView(saleType);

            TextView saleTotal = new TextView(this);
            saleTotal.setGravity(Gravity.CENTER);
            saleTotal.setTextSize(16);
            saleTotal.setText(String.format("%.2f", sale.getSaleTotal()));
            row.addView(saleTotal);

            TextView saleDetail = new TextView(this);
            saleDetail.setGravity(Gravity.CENTER);
            saleDetail.setTextSize(16);
            saleDetail.setText(getSaleDetailText(sale));
            setTextViewToStrikethroughIfSaleDetailIsMoot(saleDetail, sale);
            row.addView(saleDetail);

            salesTable.addView(row, i);
        }
    }

    //REPEATED SOMEWHERE?
    private String getSaleDetailText(Sale sale){
        if (sale instanceof CreditSale){
            return String.format("%.2f", ((CreditSale) sale).getCreditTip());
        }
        else if (sale instanceof CheckSale){
            return String.format("%.2f", ((CheckSale) sale).getCheckAmount());
        }
        else return "";
    }

    private void setTextViewToStrikethroughIfSaleDetailIsMoot(TextView view, Sale sale){
        if (sale instanceof CreditSale){
            if (((CreditSale) sale).getCreditTip() == 0){
                view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        else if (sale instanceof CheckSale){
            if (sale.getSaleTotal() == ((CheckSale) sale).getCheckAmount()){
                view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

//    private void saveValuesToSavedPreferences(){
//        SharedPreferences.Editor sharedPrefEditor = getPreferences(MODE_PRIVATE).edit();
//
//        String preferenceString = SalesListSharedPreferenceAdaptor.buildPreferenceString(salesList.getSalesList());
//        sharedPrefEditor.putString("ADAPTOR_SALES_LIST", preferenceString);
//        sharedPrefEditor.apply();
//    }

//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item){
//        int id = item.getItemId();
//        if (id == R.id.view_sales_statistics_menu_item){
//            //Open Sale Statistics Screen
//            return true;
//        }
//        else if (id == R.id.exit_menu_item){
//            finish();
//            return true;
//        }
//        else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
}
