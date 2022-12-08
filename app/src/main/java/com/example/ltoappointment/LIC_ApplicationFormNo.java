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
import android.widget.TableLayout;
import android.widget.Toast;

public class LIC_ApplicationFormNo extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    Spinner licenseType;
    Button nextButton, backButton, uf1, uf2, uf3, uf4, uf5, uf6, uf7, uf8, uf9, uf10, uf11, uf12, uf13, uf14, uf15, uf16;
    TableLayout sp_new, sp_renew, sp_upgrade;
    SharedPreferences storage;
    Intent myFile;

    public static final String SHARED_PREF = "shared preferences";
    public static final String Transaction = "transaction_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lic_application_form);

        licenseType = findViewById(R.id.license_type);
        nextButton = findViewById(R.id.nextBtn);
        backButton = findViewById(R.id.backBtn);
        sp_new = findViewById(R.id.requirements_sp_new);
        sp_renew = findViewById(R.id.requirements_sp_renew);
        sp_upgrade = findViewById(R.id.requirements_sp_upgrade);

        uf1 = findViewById(R.id.uf_1);
        uf2 = findViewById(R.id.uf_2);
        uf3 = findViewById(R.id.uf_3);
        uf4 = findViewById(R.id.uf_4);
        uf5 = findViewById(R.id.uf_5);
        uf6 = findViewById(R.id.uf_6);
        uf7 = findViewById(R.id.uf_7);
        uf8 = findViewById(R.id.uf_8);
        uf9 = findViewById(R.id.uf_9);
        uf10 = findViewById(R.id.uf_10);
        uf11 = findViewById(R.id.uf_11);
        uf12 = findViewById(R.id.uf_12);
        uf13 = findViewById(R.id.uf_13);
        uf14 = findViewById(R.id.uf_14);
        uf15 = findViewById(R.id.uf_15);
        uf16 = findViewById(R.id.uf_16);

        //local storage to cache data
        storage = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);


        String[] licensetype = new String[] {" ", "Student-Driver's Permit New", "Student-Driver's Permit Renewal", "Student-Driver's Permit Upgrade (Non-Professional License)"};

        ArrayAdapter<String> license = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, licensetype);
        license.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        licenseType.setAdapter(license);
        licenseType.setOnItemSelectedListener(this);


        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        uf1.setOnClickListener(this);
        uf2.setOnClickListener(this);
        uf3.setOnClickListener(this);
        uf4.setOnClickListener(this);
        uf5.setOnClickListener(this);
        uf6.setOnClickListener(this);
        uf7.setOnClickListener(this);
        uf8.setOnClickListener(this);
        uf9.setOnClickListener(this);
        uf10.setOnClickListener(this);
        uf11.setOnClickListener(this);
        uf12.setOnClickListener(this);
        uf13.setOnClickListener(this);
        uf14.setOnClickListener(this);
        uf15.setOnClickListener(this);
        uf16.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String permitType = licenseType.getSelectedItem().toString();

        //condition for sp new
        if(permitType.equals("Student-Driver's Permit New")){
            sp_new.setVisibility(View.VISIBLE);
        }
        else{
            sp_new.setVisibility(View.INVISIBLE);
        }

        //condition for sp renew
        if(permitType.equals("Student-Driver's Permit Renewal")){
            sp_renew.setVisibility(View.VISIBLE);
        }
        else{
            sp_renew.setVisibility(View.INVISIBLE);
        }

        //condition for sp upgrade
        if(permitType.equals("Student-Driver's Permit Upgrade (Non-Professional License)")){
            sp_upgrade.setVisibility(View.VISIBLE);
        }
        else{
            sp_upgrade.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        String permitType = licenseType.getSelectedItem().toString();

        if(v.getId() == R.id.nextBtn){
            if(permitType.equals(" ")){
                Toast.makeText(this, "Please fill out application", Toast.LENGTH_SHORT).show();
            }
            else{
                //storing data or input
                SharedPreferences.Editor editor = storage.edit();
                editor.putString("license_type", permitType);
                editor.apply();
                startActivity(new Intent(LIC_ApplicationFormNo.this, Calendar.class));
            }
        }
        else if(v.getId() == R.id.backBtn){
            startActivity(new Intent(LIC_ApplicationFormNo.this, Homepage.class));
        }
        else if(v.getId() == R.id.uf_1 || v.getId() == R.id.uf_2 || v.getId() == R.id.uf_3 || v.getId() == R.id.uf_4 ||
                v.getId() == R.id.uf_5 || v.getId() == R.id.uf_6 || v.getId() == R.id.uf_7 || v.getId() == R.id.uf_8){
            myFile = new Intent(Intent.ACTION_GET_CONTENT);
            myFile.setType("*/*");
            startActivityForResult(myFile, 10);
        }
        else if(v.getId() == R.id.uf_9 || v.getId() == R.id.uf_10 || v.getId() == R.id.uf_11 || v.getId() == R.id.uf_12 ||
                v.getId() == R.id.uf_13 || v.getId() == R.id.uf_14 || v.getId() == R.id.uf_15 || v.getId() == R.id.uf_16){
            myFile = new Intent(Intent.ACTION_GET_CONTENT);
            myFile.setType("*/*");
            startActivityForResult(myFile, 10);
        }
    }
}