package com.example.ltoappointment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "TAG";
    EditText lastname, firstname, middlename, birthdate, nationality, email, pass, confirmPass;
    RadioGroup gender;
    RadioButton radioButton;
    Button signup;
    TextView login;
    DatePickerDialog pickBirthday;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    CardView check1, check2, check3, check4;

    String roles;

    private boolean  isAtleast8 = false;
    private boolean hasUpperCase = false;
    private boolean hasNumber = false;
    private boolean hasSymbol = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        lastname = findViewById(R.id.lastName);
        firstname = findViewById(R.id.firstName);
        middlename = findViewById(R.id.middleInitial);
        birthdate = findViewById(R.id.birthday);
        email = findViewById(R.id.emailAdd);
        nationality = findViewById(R.id.nationality_field);
        pass = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPassword);
        gender = findViewById(R.id.rg_gender);

        check1 = findViewById(R.id.checkOne);
        check2 = findViewById(R.id.checkTwo);
        check3 = findViewById(R.id.checkThree);
        check4 = findViewById(R.id.checkFour);

        signup = findViewById(R.id.signupButton);
        login = findViewById(R.id.loginHere);

        birthdate.setOnClickListener(this);
        signup.setOnClickListener(this);
        login.setOnClickListener(this);

        //check if the user is already existing
        if(fAuth.getCurrentUser() != null){
            Toast.makeText(this, "Existing User, Please login", Toast.LENGTH_SHORT).show();
        }
        inputChange();
    }
    public void signupHere(){

        //for gender radio group
        //String genderTxt = (String) radioButton.getText();

        String lastnameTxt = lastname.getText().toString();
        String firstnameTxt = firstname.getText().toString();
        String middlenameTxt = middlename.getText().toString();
        String emailTxt = email.getText().toString().trim();
        String birthdayTxt = birthdate.getText().toString();
        String nationalTxt = nationality.getText().toString();
        String passTxt = pass.getText().toString().trim();
        String confirmPassTxt = confirmPass.getText().toString().trim();


        if(TextUtils.isEmpty(lastnameTxt)){
            lastname.setError("Last Name is Required");
            lastname.requestFocus();
        }
        else if(TextUtils.isEmpty(firstnameTxt)){
            firstname.setError("First Name is Required");
            firstname.requestFocus();
        }
        else if(TextUtils.isEmpty(middlenameTxt)){
            middlename.setError("Middle Name is Required, put n/a for not applicable");
            middlename.requestFocus();
        }
        else if(TextUtils.isEmpty(nationalTxt)){
            nationality.setError("Nationality is Required");
            nationality.requestFocus();
        }
        else if(TextUtils.isEmpty(emailTxt)){
            email.setError("Email is Required");
            email.requestFocus();
        }
        else if(TextUtils.isEmpty(birthdayTxt)){
            birthdate.setError("Birthday is Required");
            birthdate.requestFocus();
        }
        else if(TextUtils.isEmpty(passTxt)){
            pass.setError("Please set your password");
            pass.requestFocus();
        }
        else if(TextUtils.isEmpty(confirmPassTxt)){
            confirmPass.setError("Please confirm your password");
            confirmPass.requestFocus();
        }
        else if(!passTxt.equals(confirmPassTxt)){
            confirmPass.setError("Password not matched");
        }
        else{
            // Firebase authentication, creating new account
            fAuth.createUserWithEmailAndPassword(emailTxt,passTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        //inserting to firestore
                        roles = fAuth.getCurrentUser().getUid();
                        DocumentReference ref = db.collection("Users").document("Costumer")
                                .collection("Costumer_Accounts").document(roles);

                        Map<String, Object> user = new HashMap<>();
                        user.put("user_LN", lastnameTxt);
                        user.put("user_FN", firstnameTxt);
                        user.put("user_MN", middlenameTxt);
                        user.put("user_B", birthdayTxt);
                        //user.put("user_G", genderTxt);
                        user.put("user_N", nationalTxt);
                        //user.put("user_A", addressTxt);
                        user.put("user_E", emailTxt);
                        user.put("user_PWD", passTxt);

                        ref.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //sending email verification link
                                FirebaseUser fUser = fAuth.getCurrentUser();
                                assert fUser != null;
                                fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Signup.this, "Check email for verification", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Email not sent" + e.getMessage());
                                    }
                                });

                                Log.d(TAG, "onSuccess: User profile is created for " + roles);
                                startActivity(new Intent(Signup.this, Login.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });

                        startActivity(new Intent(Signup.this,Login.class));
                    }
                    else{
                        Toast.makeText(Signup.this, "Error Signing Up" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.signupButton){
            signupHere();
        }
        else if(v.getId() == R.id.birthday){
            Calendar calendar = java.util.Calendar.getInstance();

            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            pickBirthday = new DatePickerDialog(Signup.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    String birth = (month+1) + "/" + dayOfMonth + "/" + year;
                    birthdate.setText(birth);
                }
            },month, day, year);
            pickBirthday.show();
        }
        else if(v.getId() == R.id.loginHere){
            startActivity(new Intent(Signup.this, Login.class));
        }
        else{
            Toast.makeText(this, "Please Signup", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ResourceType")
    public void passwordCheck(){
        String passTxt = pass.getText().toString();

        // for 8 characters
        if(passTxt.length() >= 8){
            isAtleast8 = true;
            check1.setCardBackgroundColor(Color.parseColor(getString(R.color.colorAccent)));
        }else{
            isAtleast8 = false;
            check1.setCardBackgroundColor(Color.parseColor(getString(R.color.colorDefault)));
        }

        //for upper case
        if(passTxt.matches("(.*[A-Z].*)")){
            hasUpperCase = true;
            check2.setCardBackgroundColor(Color.parseColor(getString(R.color.colorAccent)));
        }else{
            hasUpperCase = false;
            check2.setCardBackgroundColor(Color.parseColor(getString(R.color.colorDefault)));
        }

        //for number
        if(passTxt.matches("(.*[0-9].*)")){
            hasNumber = true;
            check3.setCardBackgroundColor(Color.parseColor(getString(R.color.colorAccent)));
        }else{
            hasNumber = false;
            check3.setCardBackgroundColor(Color.parseColor(getString(R.color.colorDefault)));
        }

        //for symbol
        if(passTxt.matches("^(?=.*[_.()]).*")){
            hasSymbol = true;
            check4.setCardBackgroundColor(Color.parseColor(getString(R.color.colorAccent)));
        }else{
            hasSymbol = false;
            check4.setCardBackgroundColor(Color.parseColor(getString(R.color.colorDefault)));
        }
    }

    public void inputChange(){
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}