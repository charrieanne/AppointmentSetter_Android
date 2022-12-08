package com.example.ltoappointment;

import static com.example.ltoappointment.Signup.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText email, pass;
    Button login;
    TextView signup, forgotpass;
    FirebaseAuth fAuth;
    FirebaseUser user;

    boolean passVisible;
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

        pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= pass.getRight() - pass.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = pass.getSelectionEnd();
                        if(passVisible){
                            //change to invisible icon
                            pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off_24, 0);

                            //hide password
                            pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passVisible = false;
                        }
                        else{
                            //change to visible icon
                            pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.visibility_on_24,0);

                            //show password
                            pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passVisible = true;
                        }
                        pass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

         /*
        if(!user.isEmailVerified()){
            verifyNow.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            verifyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(v.getContext(), "Email Verification has been Sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email verification link not sent " + e.getMessage());
                        }
                    });
                    fAuth.signOut();
                }
            });


        }

         */
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
        else{
            //Login with existing account - authentication
            fAuth.signInWithEmailAndPassword(emailTxt,passTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // send email verification link
                    FirebaseUser fusers = fAuth.getCurrentUser();
                    if(task.isSuccessful()){
                        assert fusers != null;
                        if(fusers.isEmailVerified()){
                            Toast.makeText(Login.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,Homepage.class));
                        }
                        else{
                            showAlertDialog();
                            //fusers.sendEmailVerification();
                            //Toast.makeText(Login.this, "Verify your email first before logging-in", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(Login.this, "Login Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email first before logging-in");

        //send verification when continue is clicked
        builder.setPositiveButton("Send Verification", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send email verification link
                FirebaseUser fusers = fAuth.getCurrentUser();
                assert fusers != null;
                fusers.sendEmailVerification();
                Toast.makeText(Login.this, "Verification link has been sent", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
               builder.create().show();

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