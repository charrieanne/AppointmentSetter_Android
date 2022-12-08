package com.example.ltoappointment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class Calendar extends AppCompatActivity implements View.OnClickListener{

    DatePicker datePicker;
    Button pickButton, backButton;
    SharedPreferences storage;
    TextView label;

    public static final String SHARED_PREF = "shared preferences";

    public static final String Transaction = "transaction_type";
    public static final String License = "license_type";
    public static final String lic_application = "lic_application_type";
    public static final String mvr_application = "mvr_application_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        datePicker = findViewById(R.id.date_Picker);
        pickButton = findViewById(R.id.pickBtn);
        backButton = findViewById(R.id.backBtn);
        label = findViewById(R.id.licensing);

        //disable dates before today
        java.util.Calendar today = java.util.Calendar.getInstance();
        long now = today.getTimeInMillis();
        datePicker.setMinDate(now);


        //disable all SATURDAYS and SUNDAYS
        for (java.util.Calendar loopdate = today; today.before(today); today.add(java.util.Calendar.DATE, 1), loopdate = today){
            int dayOfWeek = loopdate.get(java.util.Calendar.DAY_OF_WEEK);
            if(dayOfWeek == java.util.Calendar.SUNDAY || dayOfWeek == java.util.Calendar.SATURDAY){
                java.util.Calendar[] disableDays = new java.util.Calendar[1];
                disableDays[0] = loopdate;

            }
        }
        // local storage to cache data
        storage = getApplicationContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

/*
        String transactionField = storage.getString("transaction_type", "");
        String licenseField = storage.getString("license_type", "");
        String licenseApplicationType = storage.getString("lic_application_type", "");

        String licenseApplication = licenseField + " " + licenseApplicationType;

        if(storage.contains(Transaction)){
            storage_testing1.setText(transactionField);
        }
        if(storage.contains(lic_application) && storage.contains(License)){
            storage_testing2.setText(licenseApplication);
        }
*/

        pickButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

    }

    public String getDate(){
        StringBuilder builder = new StringBuilder();
        builder.append(datePicker.getMonth() + 1).append(" / "); //month is 0 based
        builder.append(datePicker.getDayOfMonth()).append(" / ");
        builder.append(datePicker.getYear());
        return builder.toString();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.pickBtn) {
            //store input
            SharedPreferences.Editor editor = storage.edit();
            editor.putString("date", getDate());
            editor.apply();

            Toast.makeText(this, "Your appointment date is " + getDate() + " Please choose a time", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Calendar.this, TimeSchedule.class));
        }
        else if(v.getId() == R.id.backBtn){
            startActivity(new Intent(Calendar.this, LIC_ApplicationForm.class));
        }

    }
}