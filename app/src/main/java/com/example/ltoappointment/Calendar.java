package com.example.ltoappointment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Calendar extends AppCompatActivity implements View.OnClickListener{

    Button proceedButton, backButton;
    TextView label,date;
    DatePickerDialog pickDate;
    Dialog confirmation;
    DrawerLayout drawerLayout;

    private SharedPreferences storage;
    private FirebaseFirestore db_appointment;
    FirebaseAuth fAuth;
    String currentid;

    //local storage strings
    public static final String SHARED_PREF = "shared preferences";
    public static final String Transaction = "transaction_type";
    public static final String Date = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db_appointment = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        drawerLayout =findViewById(R.id.calendarDrawer);
        proceedButton = findViewById(R.id.proceedBtn);
        backButton = findViewById(R.id.backBtn);
        label = findViewById(R.id.licensing);
        date = findViewById(R.id.dateAppointment);

        confirmation = new Dialog(this);
        confirmation.setCancelable(true);

        // disable dates before today
        //java.util.Calendar today = java.util.Calendar.getInstance();
       // long now = today.getTimeInMillis();
        //pickDate.setMinDate(now);


        // local storage to cache data
        storage = getApplicationContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        date.setOnClickListener(this);
        proceedButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

    }

    //for navigation drawer menu
    public  void ClickMenu(View view){
        openMenu(drawerLayout);
    }
    @SuppressLint("RtlHardcoded")
    public static void openMenu(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void ClickBack(View view){
        closeDrawer(drawerLayout);
    }
    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public void ClickHome(View view){
        redirectActivity(this, Homepage.class);
    }
    public void ClickProfile(View view){
        redirectActivity(this, Profile.class);
    }
    private static void redirectActivity(Activity activity, Class Class) {
        Intent intent = new Intent(activity, Class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    public void ClickLogout(View view){
        fAuth.signOut();
        startActivity(new Intent(Calendar.this, MainActivity.class));
        finish();
    }
    public void ClickPortal(View view){
        OpenPortal("https://lto.gov.ph/frequently-asked-questions/license-permit.html");
    }
    public void OpenPortal(String url){
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
    public void ClickMyAppointment(View view){
        redirectActivity(this, MyAppointment.class);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.dateAppointment ){
            java.util.Calendar calendar = java.util.Calendar.getInstance();

            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);


            pickDate = new DatePickerDialog(Calendar.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String appointmentDate = (month+1) + "-" + dayOfMonth + "-" + year;
                    date.setText(appointmentDate);
                }
            },month, day, year);
            pickDate.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
            pickDate.show();

            /*
            //Add 6 days with current date
            c.add(Calendar.DAY_OF_MONTH,6);

            //Set the maximum date to select from DatePickerDialog
            dp.setMaxDate(c.getTimeInMillis());
            //Now DatePickerDialog have 7 days range to get selection any one from those dates

             */
        }
        else if(v.getId() == R.id.proceedBtn) {
            String dateAppointments = date.getText().toString();

            //get current date
            java.util.Date date = new Date();
            String currentDate = new SimpleDateFormat("MM-dd-yyy", Locale.getDefault()).format(date);


            //store input
            SharedPreferences.Editor editor = storage.edit();
            editor.putString("date", dateAppointments);
            editor.apply();

            //pop-up window, confirmation
            Button confirm, cancel;
            TextView nameClient, transactionAppointment, dateAppointment, number, emailadd;

            confirmation.setContentView(R.layout.confirmation_popup);
            confirm = confirmation.findViewById(R.id.confirmBtn);
            cancel = confirmation.findViewById(R.id.cancelBtn);

            nameClient = confirmation.findViewById(R.id.fullName);
            transactionAppointment = confirmation.findViewById(R.id.transactionType);
            dateAppointment = confirmation.findViewById(R.id.dateAppointment);
            emailadd = confirmation.findViewById(R.id.emailAdd);
            number = confirmation.findViewById(R.id.contactNumber);

            //retrieve from local storage
            String dateField = storage.getString("date", "");
            String transactionField = storage.getString("transaction_type", "");

            currentid = fAuth.getCurrentUser().getUid();

            DocumentReference ref = db_appointment.collection("Users").document("Applicant").collection("ApplicantData").document(currentid);
            ref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    assert value != null;
                    String Lname = value.getString("user_LN");
                    String Fname = value.getString("user_FN");
                    String Mname = value.getString("user_MN");

                    String name = Fname + " " + Mname + " " + Lname;
                    nameClient.setText(name);
                    emailadd.setText(value.getString("user_E"));
                    number.setText(value.getString("user_CN"));
                }
            });

                //for date label
                if(storage.contains(Transaction)){
                    transactionAppointment.setText(transactionField);

                    //for date label
                    if(storage.contains(Date)){
                        dateAppointment.setText(dateField);
                    }
                }
                dateAppointment.setText(dateAppointments);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String transactionTxt = transactionAppointment.getText().toString();

                    RandomString randomString = new RandomString();
                    String result = randomString.generateAlphanumeric(6);


                    currentid = fAuth.getCurrentUser().getUid();

                    Map<String, Object> appointment = new HashMap<>();
                    //appointment.put("users_id", nameTxt);
                    appointment.put("A_tt", transactionTxt);
                    appointment.put("A_dateAppointment", dateAppointments);
                    appointment.put("A_dateApplication", currentDate);
                    appointment.put("user_ID", currentid);
                    appointment.put("A_Stat", "Pending");
                    appointment.put("A_AppID", result);

                    db_appointment.collection("Appointment")
                            .add(appointment)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(Calendar.this, "Your application request has been sent successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Calendar.this, "Application Request Unsuccessful", Toast.LENGTH_SHORT).show();
                                }
                            });
                    startActivity(new Intent(Calendar.this, Homepage.class));
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmation.dismiss();
                }
            });
            confirmation.show();

        }
        else if(v.getId() == R.id.backBtn){
            startActivity(new Intent(Calendar.this, Homepage.class));
        }

    }
}