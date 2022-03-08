package com.nttdata.lagm.movements.util;

public class Constants {
	// Types of Banking Movements
	public static final int ID_BANK_PRODUCT_ACCOUNT_DEPOSIT = 1;
	public static final int ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL = 2;
	public static final int ID_BANK_PRODUCT_CREDIT_PAYMENT = 3;
	public static final int ID_BANK_PRODUCT_CREDIT_CHARGE = 4;
	
	// Description of Banking Movements
	public static final String BANK_PRODUCT_ACCOUNT_DEPOSIT_DESCRIPTION = "Depósito";
	public static final String ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL_DESCRIPTION = "Retiro";
	public static final String ID_BANK_PRODUCT_CREDIT_PAYMENT_DESCRIPTION = "Pago";
	public static final String ID_BANK_PRODUCT_CREDIT_CHARGE_DESCRIPTION = "Cargo";
	
	// Types of Bank Product
	public static final int ID_BANK_PRODUCT_ACCOUNT = 1;
	public static final int ID_BANK_PRODUCT_CREDIT = 2;
	
	// Description of Bank Product
	public static final String BANK_PRODUCT_ACCOUNT_DESCRIPTION = "Cuenta bancaria";
	public static final String BANK_PRODUCT_CREDIT_DESCRIPTION = "Crédito";
	
	// Types of Bank Account
	public static final int ID_BANK_ACCOUNT_SAVING = 1;
	public static final int ID_BANK_ACCOUNT_CURRENT_ACCOUNT = 2;
	public static final int ID_BANK_ACCOUNT_FIXED_TERM = 3;

	// Description Bank Account
	public static final String BANK_ACCOUNT_SAVING_DESCRIPTION = "Cuenta de Ahorro";
	public static final String BANK_ACCOUNT_CURRENT_ACCOUNT_DESCRIPTION = "Cuenta corriente";
	public static final String BANK_ACCOUNT_FIXED_TERM_DESCRIPTION = "Plazo Fijo";

}
