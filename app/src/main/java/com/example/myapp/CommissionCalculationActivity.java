package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Simple class to hold Employee ID and Name for Spinner
class EmployeeSpinnerItem {
    long id;
    String name;

    public EmployeeSpinnerItem(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name; // This is what will be displayed in the Spinner
    }

    public long getId() {
        return id;
    }
}

public class CommissionCalculationActivity extends AppCompatActivity {

    Spinner spinnerRepresentative;
    EditText editTextMonth, editTextYear, editTextSalesQuantity, editTextSaleRegion;
    Button buttonCalculateSaveCommission;
    TextView textViewCalculatedCommission;

    DatabaseHelper dbHelper;
    List<EmployeeSpinnerItem> employeeList;
    ArrayAdapter<EmployeeSpinnerItem> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_calc);

        dbHelper = new DatabaseHelper(this);

        spinnerRepresentative = findViewById(R.id.spinnerRepresentative);
        editTextMonth = findViewById(R.id.editTextMonth);
        editTextYear = findViewById(R.id.editTextYear);
        editTextSalesQuantity = findViewById(R.id.editTextSalesQuantity);
        editTextSaleRegion = findViewById(R.id.editTextSaleRegion); // Added this
        buttonCalculateSaveCommission = findViewById(R.id.buttonCalculateSaveCommission);
        textViewCalculatedCommission = findViewById(R.id.textViewCalculatedCommission);

        loadEmployeesIntoSpinner();

        buttonCalculateSaveCommission.setOnClickListener(v -> calculateAndSaveCommission());
    }

    private void loadEmployeesIntoSpinner() {
        employeeList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_EMPLOYEES,
                new String[]{DatabaseHelper.COLUMN_EMPLOYEE_ID, DatabaseHelper.COLUMN_EMPLOYEE_NAME},
                null, null, null, null, DatabaseHelper.COLUMN_EMPLOYEE_NAME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMPLOYEE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMPLOYEE_NAME));
                employeeList.add(new EmployeeSpinnerItem(id, name));
            }
            cursor.close();
        }
        db.close();

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employeeList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepresentative.setAdapter(spinnerAdapter);

        if (employeeList.isEmpty()) {
            Toast.makeText(this, "No representatives found. Please add representatives first.", Toast.LENGTH_LONG).show();
            buttonCalculateSaveCommission.setEnabled(false);
        }
    }

    private boolean validateInput() {
        if (spinnerRepresentative.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a representative.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextMonth.getText().toString().trim().isEmpty()) {
            editTextMonth.setError("Month cannot be empty");
            return false;
        }
        if (editTextYear.getText().toString().trim().isEmpty()) {
            editTextYear.setError("Year cannot be empty");
            return false;
        }
        if (editTextSalesQuantity.getText().toString().trim().isEmpty()) {
            editTextSalesQuantity.setError("Sales quantity cannot be empty");
            return false;
        }
        if (editTextSaleRegion.getText().toString().trim().isEmpty()) { // Added this
            editTextSaleRegion.setError("Sale region cannot be empty");
            return false;
        }

        try {
            int month = Integer.parseInt(editTextMonth.getText().toString().trim());
            if (month < 1 || month > 12) {
                editTextMonth.setError("Month must be between 1 and 12");
                return false;
            }
            int year = Integer.parseInt(editTextYear.getText().toString().trim());
            if (year < 2000 || year > Calendar.getInstance().get(Calendar.YEAR) + 5) { // Basic year validation
                 editTextYear.setError("Enter a valid year");
                 return false;
            }
            Double.parseDouble(editTextSalesQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format in month, year, or sales quantity.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void calculateAndSaveCommission() {
        if (!validateInput()) {
            return;
        }

        EmployeeSpinnerItem selectedEmployee = (EmployeeSpinnerItem) spinnerRepresentative.getSelectedItem();
        long employeeId = selectedEmployee.getId();
        int month = Integer.parseInt(editTextMonth.getText().toString().trim());
        int year = Integer.parseInt(editTextYear.getText().toString().trim());
        double salesQuantity = Double.parseDouble(editTextSalesQuantity.getText().toString().trim());
        String saleRegion = editTextSaleRegion.getText().toString().trim(); // Added this

        // --- Save Sales Data ---
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues salesValues = new ContentValues();
        salesValues.put(DatabaseHelper.COLUMN_EMPLOYEE_ID, employeeId);
        salesValues.put(DatabaseHelper.COLUMN_SALE_MONTH, month);
        salesValues.put(DatabaseHelper.COLUMN_SALE_YEAR, year);
        salesValues.put(DatabaseHelper.COLUMN_SALE_QUANTITY, salesQuantity);
        salesValues.put(DatabaseHelper.COLUMN_SALE_REGION, saleRegion); // Added this

        long saleId = db.insert(DatabaseHelper.TABLE_MONTHLY_SALES, null, salesValues);

        if (saleId == -1) {
            Toast.makeText(this, "Error saving sales data.", Toast.LENGTH_SHORT).show();
            db.close();
            return;
        } else {
            Toast.makeText(this, "Sales data saved.", Toast.LENGTH_SHORT).show();
        }

        // --- Calculate and Save Commission (Placeholder: 10% of sales) ---
        double commissionRate = 0.10; // 10%
        double commissionAmount = salesQuantity * commissionRate;

        ContentValues commissionValues = new ContentValues();
        commissionValues.put(DatabaseHelper.COLUMN_EMPLOYEE_ID, employeeId);
        commissionValues.put(DatabaseHelper.COLUMN_COMMISSION_MONTH, month);
        commissionValues.put(DatabaseHelper.COLUMN_COMMISSION_YEAR, year);
        commissionValues.put(DatabaseHelper.COLUMN_COMMISSION_AMOUNT, commissionAmount);

        long commissionId = db.insert(DatabaseHelper.TABLE_MONTHLY_COMMISSIONS, null, commissionValues);

        if (commissionId != -1) {
            textViewCalculatedCommission.setText("Calculated Commission: " + String.format("%.2f", commissionAmount));
            Toast.makeText(this, "Commission calculated and saved!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error saving commission data.", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}
