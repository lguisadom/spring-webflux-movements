package com.nttdata.lagm.movements.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Util {
    public static String bankProductDescription(int bankProductId) {
        String description = "";

        switch (bankProductId) {
            case Constants.ID_BANK_PRODUCT_ACCOUNT:
                description = Constants.BANK_PRODUCT_ACCOUNT_DESCRIPTION;
                break;
            case Constants.ID_BANK_PRODUCT_CREDIT:
                description = Constants.BANK_PRODUCT_CREDIT_DESCRIPTION;
                break;
        }
        return description;
    }
    
    public static String bankingMovementTypeDescription(int bankingMovementTypeId) {
        String description = "";

        switch (bankingMovementTypeId) {
            case Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT:
                description = Constants.BANK_PRODUCT_ACCOUNT_DEPOSIT_DESCRIPTION;
                break;
            case Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL:
                description = Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL_DESCRIPTION;
                break;
                
            case Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT:
                description = Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT_DESCRIPTION;
                break;
                
            case Constants.ID_BANK_PRODUCT_CREDIT_CHARGE:
                description = Constants.ID_BANK_PRODUCT_CREDIT_CHARGE_DESCRIPTION;
                break;
        }
        return description;
    }
    
    public static String bankProductTypeValidInfo() {
    	return String.format("%s:%s | %s:%s", 
				Constants.ID_BANK_PRODUCT_ACCOUNT, Util.bankProductDescription(Constants.ID_BANK_PRODUCT_ACCOUNT),
				Constants.ID_BANK_PRODUCT_CREDIT, Util.bankProductDescription(Constants.ID_BANK_PRODUCT_CREDIT));
    }
    
    public static String bankMovementTypeValidInfo() {
    	return String.format("%s:%s | %s:%s | %s:%s | %s:%s", 
				Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT, Util.bankProductDescription(Constants.ID_BANK_PRODUCT_ACCOUNT),
				Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL, Util.bankProductDescription(Constants.ID_BANK_PRODUCT_CREDIT),
				Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT, Util.bankProductDescription(Constants.ID_BANK_PRODUCT_CREDIT),
				Constants.ID_BANK_PRODUCT_CREDIT_CHARGE, Util.bankProductDescription(Constants.ID_BANK_PRODUCT_CREDIT));
    }
    
    public static String getToday() {
    	return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static LocalDate getLocalDateFromStringDate(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDate localDate = LocalDate.parse(strDate, formatter);
        return localDate;
    }

    public static int getMonthFromStringDate(String strDate) {
        return getLocalDateFromStringDate(strDate).getMonthValue();
    }

    public static int getYearFromStringDate(String strDate) {
        return getLocalDateFromStringDate(strDate).getYear();
    }
}
