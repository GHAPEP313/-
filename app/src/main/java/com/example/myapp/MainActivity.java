package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent; // Required for Intent

public class MainActivity extends AppCompatActivity {

    Button addEditRepresentativeButton;
    Button calculateCommissionButton;
    Button searchSalesButton;
    Button searchCommissionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addEditRepresentativeButton = findViewById(R.id.addEditRepresentativeButton);
        calculateCommissionButton = findViewById(R.id.calculateCommissionButton);
        searchSalesButton = findViewById(R.id.searchSalesButton);
        searchCommissionsButton = findViewById(R.id.searchCommissionsButton);

        addEditRepresentativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to launch SalesRepresentativeEntryActivity (to be created)
                Intent intent = new Intent(MainActivity.this, SalesRepresentativeEntryActivity.class);
                startActivity(intent);
                // System.out.println("Navigate to Add/Edit Sales Representative Activity");
                // android.widget.Toast.makeText(MainActivity.this, "Navigate to Add/Edit Sales Representative", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        calculateCommissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to launch CommissionCalculationActivity (to be created)
                Intent intent = new Intent(MainActivity.this, CommissionCalculationActivity.class);
                startActivity(intent);
                // System.out.println("Navigate to Calculate Monthly Commission Activity");
                // android.widget.Toast.makeText(MainActivity.this, "Navigate to Calculate Monthly Commission", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        searchSalesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to launch SalesSearchActivity (to be created)
                // Intent intent = new Intent(MainActivity.this, SalesSearchActivity.class);
                // startActivity(intent);
                System.out.println("Navigate to Search Sales Activity");
                // android.widget.Toast.makeText(MainActivity.this, "Navigate to Search Sales", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        searchCommissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to launch CommissionSearchActivity (to be created)
                // Intent intent = new Intent(MainActivity.this, CommissionSearchActivity.class);
                // startActivity(intent);
                System.out.println("Navigate to Search Commissions Activity");
                // android.widget.Toast.makeText(MainActivity.this, "Navigate to Search Commissions", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
