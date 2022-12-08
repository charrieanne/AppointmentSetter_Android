package com.example.ltoappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText email, pass;
    Button login;
    TextView signup, forgotpass;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailText);
        pass = findViewById(R.id.passText);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.signupHere);
        forgotpass = findViewById(R.id.forgotPassword);

        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        forgotpass.setOnClickListener(this);

    }

    public void loginUser(){
        String emailTxt = email.getText().toString();
        String passTxt = pass.getText().toString();

        if(TextUtils.isEmpty(emailTxt)){
            email.setError("Email is Required");
            email.requestFocus();
        }
        else if(TextUtils.isEmpty(passTxt)){
            email.setError("Password is Required");
            email.requestFocus();
        }
        else if(passTxt.length() < 7){
            pass.setError("Password must be 8 characters");
        }
        else{
            //Login with existing account - authentication
            fAuth.signInWithEmailAndPassword(emailTxt,passTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this,Homepage.class));
                    }
                    else{
                        Toast.makeText(Login.this, "Login Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.loginButton){
            loginUser();
        }
        else if(v.getId() == R.id.signupHere){
            startActivity(new Intent(Login.this, Signup.class));
        }
        else if(v.getId() == R.id.forgotPassword){
            EditText resetPwd = new EditText(v.getContext());
            final AlertDialog.Builder pwdResetDialog = new AlertDialog.Builder(v.getContext());
            pwdResetDialog.setTitle("Reset Password Request");
            pwdResetDialog.setMessage("Enter your email to receive password reset link");
            pwdResetDialog.setView(resetPwd);

            pwdResetDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //extract the email and send reset link
                    String email = resetPwd.getText().toString();
                    fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Login.this, "Link has been sent to your email", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Error ! Email is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            pwdResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //cancel the reset of password
                }
            });
            pwdResetDialog.create().show();
        }
        else{
            Toast.makeText(this, "Log in to your account", Toast.LENGTH_SHORT).show();
        }
    }
}