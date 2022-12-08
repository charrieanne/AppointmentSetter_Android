package com.example.ltoappointment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button login,signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signupButton);

        login.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.loginButton){
            startActivity(new Intent(MainActivity.this, Login.class));
        }
        else if(view.getId() == R.id.signupButton){
            startActivity(new Intent(this, Signup.class));
        }
        else{
            Toast.makeText(this, "Choose an Activity", Toast.LENGTH_SHORT).show();
        }
    }
}