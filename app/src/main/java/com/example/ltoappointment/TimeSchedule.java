package com.example.ltoappointment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TimeSchedule extends AppCompatActivity implements View.OnClickListener{

    Button doneButton, backButton;
    RadioButton radioButton;
    RadioGroup radioGroup;
    Dialog confirmation;
    TextView storage_testing1, storage_testing2, storage_testing3;

    // private DocumentReference ref;
    private SharedPreferences storage;
    private FirebaseFirestore db_appointment;

    //local storage strings
    public static final String SHARED_PREF = "shared preferences";
    public static final String Transaction = "transaction_type";
    public static final String License = "license_type";
    public static final String lic_application = "lic_application_type";
    public static final String mvr_application = "mvr_application_type";
    public static final String Date = "date";
    public static final String Time = "time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_schedule);

        db_appointment = FirebaseFirestore.getInstance();

        radioGroup = findViewById(R.id.radio_group);
        backButton = findViewById(R.id.backBtn);
        doneButton = findViewById(R.id.doneBtn);

        storage_testing1 = findViewById(R.id.testing1);
        storage_testing2 = findViewById(R.id.testing2);
        storage_testing3 = findViewById(R.id.testing3);

        confirmation = new Dialog(this);
        confirmation.setCancelable(true);

        // local storage to cache data
        storage = getApplicationContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        /*
        String transactionField = storage.getString("transaction_type", "");
        String licenseField = storage.getString("license_type", "");
        String licenseApplicationType = storage.getString("lic_application_type", "");
        String mvrApplicationType = storage.getString("mvr_application_type", "");
        String dateField = storage.getString("date","");
        String licenseApplication = licenseField + " " + licenseApplicationType;

        if(storage.contains(Transaction)){
            storage_testing1.setText(transactionField);
        }
        if(storage.contains(Date)){
            storage_testing3.setText(dateField);
        }

        if(storage.contains(mvr_application)){
            storage_testing2.setText(mvrApplicationType);
        }
        else if(storage.contains(lic_application) && storage.contains(License)){
            storage_testing2.setText(licenseApplication);
        }
*/

        doneButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.doneBtn) {
            //picking time
            int radioID = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(radioID);

            String timeStorage = (String) radioButton.getText();

            //store input
            SharedPreferences.Editor editor = storage.edit();
            editor.putString("time", timeStorage);
            editor.apply();

            //pop-up window, confirmation
            Button confirm;
            TextView nameClient, transactionAppointment, applicationAppointment, dateAppointment, timeAppointment;

            confirmation.setContentView(R.layout.confirmation_popup);
            confirm = confirmation.findViewById(R.id.confirmBtn);

            nameClient = confirmation.findViewById(R.id.fullName);
            transactionAppointment = confirmation.findViewById(R.id.transactionType);
            applicationAppointment = confirmation.findViewById(R.id.applicationType);
            dateAppointment = confirmation.findViewById(R.id.dateAppointment);
            timeAppointment = confirmation.findViewById(R.id.timeAppointment);

            //retrieve from local storage
            String transactionField = storage.getString("transaction_type","");
            String mvrApplicationType = storage.getString("mvr_application_type", "");
            String dateField = storage.getString("date", "");
            String timeField = storage.getString("time", "");

            String licenseField = storage.getString("license_type", "");
            String licenseApplicationType = storage.getString("lic_application_type", "");
            String licenseApplication = licenseField + " " + licenseApplicationType;


            //for transaction label
            if(storage.contains(Transaction)){
                transactionAppointment.setText(transactionField);

                //for date label
                if(storage.contains(Date)){
                    dateAppointment.setText(dateField);

                    //for time label
                    if(storage.contains(Time)){
                        timeAppointment.setText(timeField);

                        //for appointment label
                        if(storage.contains(lic_application) && storage.contains(License)){
                            applicationAppointment.setText(licenseApplication);
                        }
                        else if(storage.contains(mvr_application)){
                            applicationAppointment.setText(mvrApplicationType);
                        }
                    }
                }
            }

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameTxt = nameClient.getText().toString();
                    String transactionTxt = transactionAppointment.getText().toString();
                    String applicationTxt = applicationAppointment.getText().toString() + transactionTxt;
                    String dateTxt = dateAppointment.getText().toString();
                    String timeTxt = timeAppointment.getText().toString();

                    String application = transactionTxt + " " + applicationTxt;
                    Map<String, Object> appointment = new HashMap<>();
                    appointment.put("users_id", nameTxt);
                    appointment.put("application_type", application);
                    appointment.put("date", dateTxt);
                    appointment.put("time", timeTxt);

                    db_appointment.collection("Appointments")
                            .add(appointment)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(TimeSchedule.this, "your appointment has been set Successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TimeSchedule.this, "Setting appointment Unsuccessful", Toast.LENGTH_SHORT).show();
                                }
                            });
                    startActivity(new Intent(TimeSchedule.this, Homepage.class));
                }
            });
            confirmation.show();
        }
        else if(v.getId() == R.id.backBtn){
            startActivity(new Intent(TimeSchedule.this, Calendar.class));
        }
    }
}