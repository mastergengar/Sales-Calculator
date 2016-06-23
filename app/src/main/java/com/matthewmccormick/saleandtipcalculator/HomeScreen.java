package com.matthewmccormick.saleandtipcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class HomeScreen extends ActionBarActivity {

    SalesList salesList = new SalesList();


    TextView lastSaleAddedTextView;
    RadioButton cashRadioButton;
    RadioButton checkRadioButton;
    RadioButton creditRadioButton;
    EditText saleTotalEditText;
    EditText totalReceivedEditText;

    @Override
    protected void onStop(){
        saveValuesToSavedPreferences();
        super.onStop();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.view_all_sales_menu_item){
            saveValuesToSavedPreferences();
            Intent intent = new Intent(this, AllSalesScreen.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.view_sales_statistics_menu_item){
            //Open Sale Statistics Screen
            return true;
        }
        else if (id == R.id.exit_menu_item){
            finish();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initiateVariables();
        refreshScreenValues();
        displayLastSaleAddedString();

        addOnClickListenersToButtons();
    }

    private void initiateVariables(){
        retrieveOrCreateSalesList();

        lastSaleAddedTextView = (TextView) findViewById(R.id.lastSaleAddedTextView);
        cashRadioButton = (RadioButton) findViewById(R.id.cashRadioButton);
        checkRadioButton = (RadioButton) findViewById(R.id.checkRadioButton);
        creditRadioButton = (RadioButton) findViewById(R.id.creditRadioButton);
        saleTotalEditText = (EditText) findViewById(R.id.saleTotalEditText);
        totalReceivedEditText = (EditText) findViewById(R.id.totalReceivedEditText);
    }

    private void retrieveOrCreateSalesList(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String preferenceString = sharedPreferences.getString("ADAPTOR_SALES_LIST", "");
        salesList = SalesListSharedPreferenceAdaptor.buildSalesList(preferenceString);
    }

    private void refreshScreenValues(){
        setSaleValueStrings();
        resetRadiosAndTextBoxes();
        displayLastSaleAddedString();
    }

    private void setSaleValueStrings(){
        ((TextView) findViewById(R.id.totalSalesTextView)).setText("$" + String.format("%.2f", salesList.getTotalSales()));
        ((TextView) findViewById(R.id.moneyOwedTextView)).setText("$" + String.format("%.2f", salesList.getMoneyOwed()));
        ((TextView) findViewById(R.id.creditTipsTextView)).setText("$" + String.format("%.2f", salesList.getCreditCardTips()));
        ((TextView) findViewById(R.id.dailySalesTextView)).setText("$" + String.format("%.2f", salesList.getDailySales()));
        ((TextView) findViewById(R.id.dailyVMETextView)).setText("$" + String.format("%.2f", salesList.getDailyVME()));
    }

    private void resetRadiosAndTextBoxes(){
        cashRadioButton.setChecked(true);
        saleTotalEditText.setText("");
        totalReceivedEditText.setEnabled(false);
        totalReceivedEditText.setText("");
        totalReceivedEditText.setHint("");
    }

    private String displayLastSaleAddedString(){
        String lastSaleAddedString = "No Sales Have Been Added";
        if (!salesList.isEmpty()){
            lastSaleAddedString = "Last Sale added:\n" +
                    salesList.getLastSaleAdded().toString().replace("; ", "\n");
        }

        lastSaleAddedTextView.setText(lastSaleAddedString);
        return lastSaleAddedString;
    }

    private void addOnClickListenersToButtons(){
        Button addSaleButton = (Button) findViewById(R.id.addSaleButton);
        addSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!saleTotalEditText.getText().toString().equals("")) {
                    addNewSaleValuesToVariables();
                    refreshScreenValues();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                else{
                    displayToastMessage("No Value Entered For Sale");
                }
            }
        });

        Button removeLastSaleButton = (Button) findViewById(R.id.removeLastSaleButton);
        removeLastSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!salesList.isEmpty()) {
                    removeLastSalesValuesFromVariables();
                } else {
                    lastSaleAddedTextView.setText("No Sales Have Been Added");
                    displayToastMessage("No Sales Have Been Added");
                }
            }
        });

        Button resetSalesButton = (Button) findViewById(R.id.resetSalesButton);
        resetSalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salesList = new SalesList();
                saveValuesToSavedPreferences();
                refreshScreenValues();
                lastSaleAddedTextView.setText("");
                displayToastMessage("Sales Have Been Reset");
            }
        });
    }

    private void addNewSaleValuesToVariables(){
        double saleTotal =  Double.parseDouble(saleTotalEditText.getText().toString());

        if (cashRadioButton.isChecked()){
            salesList.addSale(new Sale(saleTotal));
        }
        else {
            double receivedTotal = getReceivedTotal();

            if (checkRadioButton.isChecked()){
                if (receivedTotal == 0)
                    salesList.addSale(new CheckSale(saleTotal, saleTotal));
                else
                    salesList.addSale(new CheckSale(saleTotal, receivedTotal));
            }
            else{
                salesList.addSale(new CreditSale(saleTotal, receivedTotal));
            }
        }

        saveValuesToSavedPreferences();
        displayToastMessage(displayLastSaleAddedString());
    }

    private double getReceivedTotal(){
        if (totalReceivedEditText.getText().toString().equals(""))
            return 0;
        else
            return Double.parseDouble(totalReceivedEditText.getText().toString());
    }

    private void removeLastSalesValuesFromVariables(){
        Sale removedSale = salesList.removeLastAddedSale();
        String removedSaleString = "Sale removed:\n" +
                removedSale.toString().replace("; ", "\n");

        displayToastMessage(removedSaleString);
        saveValuesToSavedPreferences();
        refreshScreenWithRemovedSale(removedSale);
    }

    private void refreshScreenWithRemovedSale(Sale removedSale){
        setSaleValueStrings();
        resetRadiosAndTextBoxesWithRemovedSale(removedSale);
        displayLastSaleAddedString();
    }

    private void resetRadiosAndTextBoxesWithRemovedSale(Sale removedSale){
        saleTotalEditText.setText(String.format("%.2f", removedSale.getSaleTotal()));

        if (removedSale instanceof CreditSale){
            double creditTip = ((CreditSale) removedSale).getCreditTip();
            if (creditTip != 0) {
                totalReceivedEditText.setText(String.format("%.2f", creditTip));
            }

            creditRadioButton.setChecked(true);
            totalReceivedEditText.setEnabled(true);
        }
        else if(removedSale instanceof CheckSale){
            double checkAmount = ((CheckSale) removedSale).getCheckAmount();
            if (checkAmount != removedSale.getSaleTotal()) {
                totalReceivedEditText.setText(String.format("%.2f", checkAmount));
            }

            checkRadioButton.setChecked(true);
            totalReceivedEditText.setEnabled(true);
        }
        else{
            totalReceivedEditText.setText("");
            cashRadioButton.setChecked(true);
            totalReceivedEditText.setEnabled(false);
        }
    }

    private void displayToastMessage(String message){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    private void saveValuesToSavedPreferences(){
        SharedPreferences.Editor sharedPrefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        String preferenceString = SalesListSharedPreferenceAdaptor.buildPreferenceString(salesList.getSalesList());
        sharedPrefEditor.putString("ADAPTOR_SALES_LIST", preferenceString);
        sharedPrefEditor.apply();
    }

    public void onRadioButtonClicked(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (cashRadioButton.isChecked()){
            totalReceivedEditText.setEnabled(false);
            totalReceivedEditText.setHint("");
            saleTotalEditText.requestFocus();
            imm.showSoftInput(saleTotalEditText, InputMethodManager.SHOW_IMPLICIT);
        }
        else if (checkRadioButton.isChecked()){
            totalReceivedEditText.setEnabled(true);
            totalReceivedEditText.setHint("Total On Check");
            requestFocusOnSaleTotalOrReceivedTotal(imm);
        }
        else if (creditRadioButton.isChecked()){
            totalReceivedEditText.setEnabled(true);
            totalReceivedEditText.setHint("Tip On Receipt");
            requestFocusOnSaleTotalOrReceivedTotal(imm);
        }
    }

    private void requestFocusOnSaleTotalOrReceivedTotal(InputMethodManager imm){
        if(!saleTotalEditText.getText().toString().equals("")){
            totalReceivedEditText.requestFocus();
            imm.showSoftInput(totalReceivedEditText, InputMethodManager.SHOW_IMPLICIT);
        }
        else{
            saleTotalEditText.requestFocus();
            imm.showSoftInput(saleTotalEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}