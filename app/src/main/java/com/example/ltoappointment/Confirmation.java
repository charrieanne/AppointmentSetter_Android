package com.example.ltoappointment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Confirmation extends AppCompatActivity {

    TextView name, transaction, date, time;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_popup);

        name = findViewById(R.id.fullName);
        transaction = findViewById(R.id.transactionType);
        date = findViewById(R.id.dateAppointment);



    }

}
