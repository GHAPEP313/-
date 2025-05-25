package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SalesRepresentativeEntryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText editTextName, editTextNumber, editTextRegion;
    ImageView imageViewRep;
    Button buttonSelectImage, buttonSave, buttonUpdate, buttonDelete;

    DatabaseHelper dbHelper;
    private Uri imageUri;
    private long currentEmployeeId = -1; // To store EmployeeID for update/delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_rep_entry);

        dbHelper = new DatabaseHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextRegion = findViewById(R.id.editTextRegion);
        imageViewRep = findViewById(R.id.imageViewRep);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSave = findViewById(R.id.buttonSave);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Check if an EmployeeID was passed for editing
        Intent intent = getIntent();
        if (intent.hasExtra("EMPLOYEE_ID")) {
            currentEmployeeId = intent.getLongExtra("EMPLOYEE_ID", -1);
            if (currentEmployeeId != -1) {
                loadEmployeeData(currentEmployeeId);
                buttonSave.setVisibility(View.GONE);
                buttonUpdate.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }
        } else {
            buttonSave.setVisibility(View.VISIBLE);
            buttonUpdate.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
        }

        buttonSelectImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        });

        buttonSave.setOnClickListener(v -> saveRepresentative());
        buttonUpdate.setOnClickListener(v -> updateRepresentative());
        buttonDelete.setOnClickListener(v -> deleteRepresentative());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewRep.setImageURI(imageUri);
            // Note: For simplicity, we're storing the URI as a string.
            // Real-world apps might copy the image to internal storage and store that path.
        }
    }

    private void loadEmployeeData(long employeeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_EMPLOYEES,
                null, // All columns
                DatabaseHelper.COLUMN_EMPLOYEE_ID + "=?",
                new String[]{String.valueOf(employeeId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMPLOYEE_NAME));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMPLOYEE_NUMBER));
            String region = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMPLOYEE_REGION));
            String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMPLOYEE_IMAGE_URI));

            editTextName.setText(name);
            editTextNumber.setText(number);
            editTextRegion.setText(region);
            if (imageUriString != null && !imageUriString.isEmpty()) {
                imageUri = Uri.parse(imageUriString);
                imageViewRep.setImageURI(imageUri);
            }
            cursor.close();
        }
        db.close();
    }

    private boolean validateInput() {
        String name = editTextName.getText().toString().trim();
        String number = editTextNumber.getText().toString().trim();
        String region = editTextRegion.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name cannot be empty");
            return false;
        }
        if (number.isEmpty()) {
            editTextNumber.setError("Number cannot be empty");
            return false;
        }
        if (region.isEmpty()) {
            editTextRegion.setError("Region cannot be empty");
            return false;
        }
        return true;
    }

    private void saveRepresentative() {
        if (!validateInput()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMPLOYEE_NAME, editTextName.getText().toString().trim());
        values.put(DatabaseHelper.COLUMN_EMPLOYEE_NUMBER, editTextNumber.getText().toString().trim());
        values.put(DatabaseHelper.COLUMN_EMPLOYEE_REGION, editTextRegion.getText().toString().trim());
        if (imageUri != null) {
            values.put(DatabaseHelper.COLUMN_EMPLOYEE_IMAGE_URI, imageUri.toString());
        }

        long newRowId = db.insert(DatabaseHelper.TABLE_EMPLOYEES, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(this, "Representative saved successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after saving
        } else {
            Toast.makeText(this, "Error saving representative.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRepresentative() {
        if (currentEmployeeId == -1 || !validateInput()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_EMPLOYEE_NAME, editTextName.getText().toString().trim());
        values.put(DatabaseHelper.COLUMN_EMPLOYEE_NUMBER, editTextNumber.getText().toString().trim());
        values.put(DatabaseHelper.COLUMN_EMPLOYEE_REGION, editTextRegion.getText().toString().trim());
        if (imageUri != null) {
            values.put(DatabaseHelper.COLUMN_EMPLOYEE_IMAGE_URI, imageUri.toString());
        } else {
             values.putNull(DatabaseHelper.COLUMN_EMPLOYEE_IMAGE_URI); // Or handle if image should not be cleared
        }

        int rowsAffected = db.update(DatabaseHelper.TABLE_EMPLOYEES, values,
                DatabaseHelper.COLUMN_EMPLOYEE_ID + "=?",
                new String[]{String.valueOf(currentEmployeeId)});
        db.close();

        if (rowsAffected > 0) {
            Toast.makeText(this, "Representative updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after updating
        } else {
            Toast.makeText(this, "Error updating representative.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRepresentative() {
        if (currentEmployeeId == -1) return;

        // Optional: Add a confirmation dialog here before deleting

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // First, delete related sales and commissions to maintain referential integrity
        // Or, configure CASCADE DELETE in your DB schema (not default in Android SQLite)
        db.delete(DatabaseHelper.TABLE_MONTHLY_SALES, DatabaseHelper.COLUMN_EMPLOYEE_ID + "=?", new String[]{String.valueOf(currentEmployeeId)});
        db.delete(DatabaseHelper.TABLE_MONTHLY_COMMISSIONS, DatabaseHelper.COLUMN_EMPLOYEE_ID + "=?", new String[]{String.valueOf(currentEmployeeId)});
        
        int rowsAffected = db.delete(DatabaseHelper.TABLE_EMPLOYEES,
                DatabaseHelper.COLUMN_EMPLOYEE_ID + "=?",
                new String[]{String.valueOf(currentEmployeeId)});
        db.close();

        if (rowsAffected > 0) {
            Toast.makeText(this, "Representative deleted successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        } else {
            Toast.makeText(this, "Error deleting representative.", Toast.LENGTH_SHORT).show();
        }
    }
}
