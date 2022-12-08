package com.example.ltoappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Homepage extends AppCompatActivity implements View.OnClickListener{


    Button licensing, vehicle, logout, verify;
    TextView verifyMsg;
    FirebaseAuth fAuth;
    SharedPreferences storage;
    SharedPreferences.Editor editor;
    String userID;

    public static final String SHARED_PREF = "shared preferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        fAuth = FirebaseAuth.getInstance();
        licensing = findViewById(R.id.license);
        vehicle = findViewById(R.id.registration);
        logout = findViewById(R.id.logoutButton);
        verify = findViewById(R.id.verifyButton);
        verifyMsg = findViewById(R.id.verifyTxt);


        userID = fAuth.getCurrentUser().getUid();
        FirebaseUser user = fAuth.getCurrentUser();
        if(!user.isEmailVerified()){
            verify.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser fUser = fAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Homepage.this, "Check email for verification", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent" + e.getMessage());
                        }
                    });
                }
            });
        }
        else {
            verify.setVisibility(View.INVISIBLE);
            verifyMsg.setVisibility(View.INVISIBLE);
        }
        //local storage to cache data
        storage = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        licensing.setOnClickListener(this);
        vehicle.setOnClickListener(this);
        logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String license = licensing.getText().toString();
        String vehicleRegister = vehicle.getText().toString();
        String vr_application = "Renew";

        if(v.getId() == R.id.license){
            //storing data or input
            editor = storage.edit();
            editor.putString("transaction_type", license);
            editor.apply();

            startActivity(new Intent(Homepage.this, LIC_ApplicationForm.class));
            Toast.makeText(Homepage.this, "Fill out the form for your appointment", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.registration){
            //storing data or input
            editor = storage.edit();
            editor.putString("transaction_type", vehicleRegister);
            editor.putString("mvr_application_type", vr_application);
            editor.apply();

            startActivity(new Intent(Homepage.this, Calendar.class));
            Toast.makeText(Homepage.this, "Choose a date for your appointment", Toast.LENGTH_SHORT).show();
        }
        else if(v.getId() == R.id.logoutButton){
            fAuth.signOut();
            startActivity(new Intent(Homepage.this, MainActivity.class));
        }
    }
}