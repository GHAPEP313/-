package com.example.myapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "commissions_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_EMPLOYEES = "employees";
    public static final String TABLE_MONTHLY_SALES = "monthly_sales";
    public static final String TABLE_MONTHLY_COMMISSIONS = "monthly_commissions";

    // Employees Table Columns
    public static final String COLUMN_EMPLOYEE_ID = "EmployeeID"; // Primary Key
    public static final String COLUMN_EMPLOYEE_NAME = "Name";
    public static final String COLUMN_EMPLOYEE_NUMBER = "Number";
    public static final String COLUMN_EMPLOYEE_IMAGE_URI = "ImageUri";
    public static final String COLUMN_EMPLOYEE_REGION = "Region";

    // Monthly Sales Table Columns
    public static final String COLUMN_SALE_ID = "SaleID"; // Primary Key
    // EmployeeID is a Foreign Key from Employees table
    public static final String COLUMN_SALE_MONTH = "Month";
    public static final String COLUMN_SALE_YEAR = "Year";
    public static final String COLUMN_SALE_QUANTITY = "SalesQuantity";
    public static final String COLUMN_SALE_REGION = "Region"; // Region specific to this sale

    // Monthly Commissions Table Columns
    public static final String COLUMN_COMMISSION_ID = "CommissionID"; // Primary Key
    // EmployeeID is a Foreign Key from Employees table
    public static final String COLUMN_COMMISSION_MONTH = "Month";
    public static final String COLUMN_COMMISSION_YEAR = "Year";
    public static final String COLUMN_COMMISSION_AMOUNT = "CommissionAmount";

    // Create Employees Table SQL Query
    private static final String CREATE_TABLE_EMPLOYEES = "CREATE TABLE " + TABLE_EMPLOYEES + "("
            + COLUMN_EMPLOYEE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EMPLOYEE_NAME + " TEXT,"
            + COLUMN_EMPLOYEE_NUMBER + " TEXT,"
            + COLUMN_EMPLOYEE_IMAGE_URI + " TEXT,"
            + COLUMN_EMPLOYEE_REGION + " TEXT"
            + ");";

    // Create Monthly Sales Table SQL Query
    private static final String CREATE_TABLE_MONTHLY_SALES = "CREATE TABLE " + TABLE_MONTHLY_SALES + "("
            + COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EMPLOYEE_ID + " INTEGER,"
            + COLUMN_SALE_MONTH + " INTEGER,"
            + COLUMN_SALE_YEAR + " INTEGER,"
            + COLUMN_SALE_QUANTITY + " REAL,"
            + COLUMN_SALE_REGION + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_EMPLOYEE_ID + ") REFERENCES " + TABLE_EMPLOYEES + "(" + COLUMN_EMPLOYEE_ID + ")"
            + ");";

    // Create Monthly Commissions Table SQL Query
    private static final String CREATE_TABLE_MONTHLY_COMMISSIONS = "CREATE TABLE " + TABLE_MONTHLY_COMMISSIONS + "("
            + COLUMN_COMMISSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EMPLOYEE_ID + " INTEGER,"
            + COLUMN_COMMISSION_MONTH + " INTEGER,"
            + COLUMN_COMMISSION_YEAR + " INTEGER,"
            + COLUMN_COMMISSION_AMOUNT + " REAL,"
            + "FOREIGN KEY(" + COLUMN_EMPLOYEE_ID + ") REFERENCES " + TABLE_EMPLOYEES + "(" + COLUMN_EMPLOYEE_ID + ")"
            + ");";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_EMPLOYEES);
        db.execSQL(CREATE_TABLE_MONTHLY_SALES);
        db.execSQL(CREATE_TABLE_MONTHLY_COMMISSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHLY_COMMISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTHLY_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        // Create tables again
        onCreate(db);
    }
}
