package com.example.ltoappointment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class LIC_ApplicationForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    Spinner licenseType, applicationType;
    Button nextButton, backButton;
    LinearLayout SP_new, SP_renew, drivers_new, drivers_renew, CL_new, CL_renew;
    RelativeLayout reqField;
    SharedPreferences storage;

    public static final String SHARED_PREF = "shared preferences";
    public static final String Transaction = "transaction_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lic_application_form);

        licenseType = findViewById(R.id.license_type);
        applicationType = findViewById(R.id.appointment_type);
        nextButton = findViewById(R.id.nextBtn);
        backButton = findViewById(R.id.backBtn);
        SP_new = findViewById(R.id.sp_new);
        SP_renew = findViewById(R.id.sp_renew);
        CL_new = findViewById(R.id.cl_new);
        CL_renew = findViewById(R.id.cl_renew);
        drivers_new = findViewById(R.id.p_np_new);
        drivers_renew = findViewById(R.id.p_np_renew);
        reqField = findViewById(R.id.requirements);


        //local storage to cache data
        storage = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

/*
        String transactionField = storage.getString("transaction_type", "");
        if(storage.contains(Transaction)){
            storage_testing.setText(transactionField);
        }*/

        String[] licensetype =new String[] {" ","Student-Driver's Permit","Driver's Professional License", "Driver's Non - Professional License", "Conductor's License"};
        String[] appointmenttype = new String[]{" ","New", "Renew"};

        ArrayAdapter<String> license = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, licensetype);
        license.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        licenseType.setAdapter(license);
        licenseType.setOnItemSelectedListener(this);

        ArrayAdapter<String> application = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, appointmenttype);
        application.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        applicationType.setAdapter(application);
        applicationType.setOnItemSelectedListener(this);

        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String LicenseType = licenseType.getSelectedItem().toString();
        String ApplicationType = applicationType.getSelectedItem().toString();

        // conductor's requirements, new and renew
        if(LicenseType.equals("Conductor's License") && ApplicationType.equals("New")){
            CL_new.setVisibility(CL_new.VISIBLE);
        }
        else if(LicenseType.equals("Conductor's License") && ApplicationType.equals("Renew")){
            CL_renew.setVisibility(CL_renew.VISIBLE);
        }
        else {
           CL_new.setVisibility(CL_new.INVISIBLE);
            CL_renew.setVisibility(CL_renew.INVISIBLE);
        }

        // student permit's requirements, new and renew
        if(LicenseType.equals("Student-Driver's Permit") && ApplicationType.equals("New")){
            SP_new.setVisibility(SP_new.VISIBLE);
        }
        else if(LicenseType.equals("Student-Driver's Permit") && ApplicationType.equals("Renew")){
            SP_renew.setVisibility(SP_renew.VISIBLE);
        }
        else{
            SP_new.setVisibility(SP_new.INVISIBLE);
            SP_renew.setVisibility(SP_renew.INVISIBLE);
        }

        // prof and non-prof requirements, new
        if(LicenseType.equals("Driver's Professional License") && ApplicationType.equals("New")){
            drivers_new.setVisibility(drivers_new.VISIBLE);
        }
        else if(LicenseType.equals("Driver's Non - Professional License") && ApplicationType.equals("New")){
            drivers_new.setVisibility(drivers_new.VISIBLE);
        }
        else {
            drivers_new.setVisibility(drivers_new.INVISIBLE);
        }

        // prof and non-prof requirements, renew
        if(LicenseType.equals("Driver's Professional License") && ApplicationType.equals("Renew")){
                drivers_renew.setVisibility(drivers_renew.VISIBLE);
        }
        else if(LicenseType.equals("Driver's Non - Professional License") && ApplicationType.equals("Renew")){
            drivers_renew.setVisibility(drivers_renew.VISIBLE);
        }
        else {
            drivers_renew.setVisibility(drivers_renew.INVISIBLE);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        String LicenseType = licenseType.getSelectedItem().toString();
        String ApplicationType = applicationType.getSelectedItem().toString();

        if(v.getId() == R.id.nextBtn){
            if(licenseType.getSelectedItem().equals(" ") || applicationType.getSelectedItem().equals(" ")){
                Toast.makeText(this, "Please fill out application", Toast.LENGTH_SHORT).show();
            }
            else{
                //storing data or input
                SharedPreferences.Editor editor = storage.edit();
                editor.putString("license_type", LicenseType);
                editor.putString("lic_application_type", ApplicationType);
                editor.apply();
                startActivity(new Intent(LIC_ApplicationForm.this, Calendar.class));
            }
        }
        else if(v.getId() == R.id.backBtn){
            startActivity(new Intent(LIC_ApplicationForm.this, Homepage.class));
        }
    }
}