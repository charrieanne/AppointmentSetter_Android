package com.example.ltoappointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Homepage extends AppCompatActivity implements View.OnClickListener{

    TextView appointment, name, transaction, applicationDate, appointmentDate, status;
    Button licensing, motorVehicle, logout, conductor, verifyNow, cancel, menuBTN, notifBTN;
    //TextView verifyMsg;

    SharedPreferences storage;
    SharedPreferences.Editor editor;
    Dialog popup1, popup2, popyes, popno, popconductors, popwarning1, popwarning2;
    TableLayout application;
    DrawerLayout drawerLayout;

    FirebaseFirestore db_appointment;
    FirebaseAuth fAuth;
    String currentid, pending, approved;

    public static final String SHARED_PREF = "shared preferences";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        db_appointment = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        drawerLayout =findViewById(R.id.homeDrawer);

        licensing = findViewById(R.id.license);
        motorVehicle = findViewById(R.id.mvRegistration);
        conductor = findViewById(R.id.conductors);

        name = findViewById(R.id.fullName);
        transaction = findViewById(R.id.transactionType);
        applicationDate = findViewById(R.id.dateApplication);
        appointmentDate = findViewById(R.id.dateAppointment);
        status = findViewById(R.id.applicationStatus);

        application = findViewById(R.id.applicationTBL);
        appointment = findViewById(R.id.noExisting);
        cancel = findViewById(R.id.cancel_applicationBTN);


        //verifyNow = findViewById(R.id.verifyBTN);
        //verifyMsg = findViewById(R.id.verify);

        currentid = fAuth.getCurrentUser().getUid();

        db_appointment.collection("Appointment")
                .whereEqualTo("user_ID", currentid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        for(QueryDocumentSnapshot doc : value){
                            if(Objects.equals(doc.get("A_Stat"), "Pending")){
                                transaction.setText(doc.getString("A_tt"));
                                applicationDate.setText(doc.getString("A_dateApplication"));
                                appointmentDate.setText(doc.getString("A_dateAppointment"));
                                status.setText(doc.getString("A_Stat"));

                                application.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                appointment.setVisibility(View.INVISIBLE);

                            }
                            else if(Objects.equals(doc.get("A_Stat"), "Cancelled")){
                                appointment.setVisibility(View.VISIBLE);
                                application.setVisibility(View.INVISIBLE);
                                cancel.setVisibility(View.INVISIBLE);
                            }
                            else{
                                Toast.makeText(Homepage.this, "Apply for a appointment", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        DocumentReference ref2 = db_appointment.collection("Users").document("Applicant")
                .collection("ApplicantData").document(currentid);


        ref2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                String Lname = value.getString("user_LN");
                String Fname = value.getString("user_FN");
                String Mname = value.getString("user_MN");

                String fullname = Fname + " " + Mname + " " + Lname;

                name.setText(fullname);
            }

        });

        //local storage to cache data
        storage = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        popup1 = new Dialog(this);
        popup1.setCancelable(true);

        popup2 = new Dialog(this);
        popup2.setCancelable(true);

        popyes = new Dialog(this);
        popyes.setCancelable(true);

        popno = new Dialog(this);
        popno.setCancelable(true);

        popconductors = new Dialog(this);
        popconductors.setCancelable(true);

        popwarning1= new Dialog(this);
        popwarning1.setCancelable(true);

        popwarning2 = new Dialog(this);
        popwarning2.setCancelable(true);

        licensing.setOnClickListener(this);
        motorVehicle.setOnClickListener(this);
        conductor.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    public void UpdateStat(String AStatus, String update){
        Map<String, Object> statDetail = new HashMap<>();
        statDetail.put("A_Stat", update);

        db_appointment.collection("Appointment")
                .whereEqualTo("A_Stat", AStatus)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            db_appointment.collection("Appointment")
                                    .document(documentID)
                                    .update(statDetail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Homepage.this, "Your application for appointment has been cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Homepage.this, "Sorry, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(Homepage.this, "Cancellation Incomplete", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        startActivity(new Intent(Homepage.this, MainActivity.class));
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

    @Override
    public void onClick(View v) {
        String vehicleRegister = "Motor Vehicle Registration Plain Renewal";

        if(v.getId() == R.id.license){

            //pop-up window, disclaimer
            Button proceed, cancel;

            popup1.setContentView(R.layout.popup_1);
            proceed = popup1.findViewById(R.id.proceedBTN);
            cancel = popup1.findViewById(R.id.cancelBTN);

            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button yes, no, cancel;
                    popup2.setContentView(R.layout.popup_2);
                    yes = popup2.findViewById(R.id.yesBTN);
                    no = popup2.findViewById(R.id.noBTN);
                    cancel = popup2.findViewById(R.id.cancelBTN);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button np_renew, np_upgrade, p_renew, cancel;
                            popyes.setContentView(R.layout.popup_yes);
                            np_renew = popyes.findViewById(R.id.np_renew_BTN);
                            np_upgrade = popyes.findViewById(R.id.np_upgrade_BTN);
                            p_renew = popyes.findViewById(R.id.p_renew_BTN);
                            cancel = popyes.findViewById(R.id.cancelBTN);

                            String NPRENEW = "Non-Professional Driver's License (RENEW)";
                            String NPUPGRADE = "Professional Driver's License (NEW)";
                            String PRENEW = "Professional Driver's License (RENEW)";

                            np_renew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //storing data or input
                                    editor = storage.edit();
                                    editor.putString("transaction_type", NPRENEW);
                                    editor.apply();

                                    startActivity(new Intent(Homepage.this, LIC_NP_RENEW.class));
                                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                                }
                            });

                            np_upgrade.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //storing data or input
                                    editor = storage.edit();
                                    editor.putString("transaction_type", NPUPGRADE);
                                    editor.apply();

                                    startActivity(new Intent(Homepage.this, LIC_NP_UPGRADE.class));
                                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                                }
                            });

                            p_renew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //storing data or input
                                    editor = storage.edit();
                                    editor.putString("transaction_type", PRENEW);
                                    editor.apply();

                                    startActivity(new Intent(Homepage.this, LIC_P_RENEW.class));
                                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popyes.dismiss();
                                }
                            });
                            popyes.show();
                        }

                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button sp_new, sp_upgrade, sp_renew, cancel;
                            popno.setContentView(R.layout.popup_no);
                            sp_new = popno.findViewById(R.id.sp_new_BTN);
                            sp_upgrade = popno.findViewById(R.id.sp_upgrade_BTN);
                            sp_renew = popno.findViewById(R.id.sp_renew_BTN);
                            cancel = popno.findViewById(R.id.cancelBTN);

                            String SPNEW = "Student-Driver's Permit (NEW)";
                            String SPUPGRADE = "Non-Professional Driver's License (NEW)";
                            String SPRENEW = "Student-Driver's Permit (RENEW)";

                            sp_new.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //storing data or input
                                    editor = storage.edit();
                                    editor.putString("transaction_type", SPNEW);
                                    editor.apply();

                                    startActivity(new Intent(Homepage.this, LIC_SP_NEW.class));
                                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                                }
                            });

                            sp_upgrade.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //storing data or input
                                    editor = storage.edit();
                                    editor.putString("transaction_type", SPUPGRADE);
                                    editor.apply();

                                    startActivity(new Intent(Homepage.this, LIC_SP_UPGRADE.class));
                                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                                }
                            });

                            sp_renew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //storing data or input
                                    editor = storage.edit();
                                    editor.putString("transaction_type", SPRENEW);
                                    editor.apply();
                                    startActivity(new Intent(Homepage.this, LIC_SP_RENEW.class));
                                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popno.dismiss();
                                }
                            });
                            popno.show();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popup2.dismiss();
                        }
                    });
                    popup2.show();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup1.dismiss();
                }
            });
            popup1.show();

        }
        else if(v.getId() == R.id.mvRegistration){
            //storing data or input
            editor = storage.edit();
            editor.putString("transaction_type", vehicleRegister);
            editor.apply();

            Button proceed, cancel;
            popup1.setContentView(R.layout.popup_1);
            proceed = popup1.findViewById(R.id.proceedBTN);
            cancel = popup1.findViewById(R.id.cancelBTN);

            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Homepage.this, VehicleRegistration.class));
                    Toast.makeText(Homepage.this, "Please submit your requirements", Toast.LENGTH_SHORT).show();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup1.dismiss();
                }
            });
            popup1.show();
        }
        else if(v.getId() == R.id.conductors){

            //pop-up window, conductors
            Button cl_new, cl_renew, cancel;
            popconductors.setContentView(R.layout.popup_conductors);
            cl_new = popconductors.findViewById(R.id.cl_new_BTN);
            cl_renew = popconductors.findViewById(R.id.cl_renew_BTN);
            cancel = popconductors.findViewById(R.id.cancelBTN);

            String CLNEW = "Conductor's License (NEW)";
            String CLRENEW = "Conductor's License (RENEW)";

            cl_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //storing data or input
                    editor = storage.edit();
                    editor.putString("transaction_type", CLNEW);
                    editor.apply();

                    startActivity(new Intent(Homepage.this, CL_NEW.class));
                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                }
            });

            cl_renew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //storing data or input
                    editor = storage.edit();
                    editor.putString("transaction_type", CLRENEW);
                    editor.apply();

                    startActivity(new Intent(Homepage.this, CL_RENEW.class));
                    Toast.makeText(Homepage.this, "Please Submit Requirements", Toast.LENGTH_SHORT).show();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popconductors.dismiss();
                }
            });
            popconductors.show();
        }
        else if(v.getId() == R.id.cancel_applicationBTN){
            appointment.setVisibility(View.VISIBLE);
            application.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);

            String AStatus = status.getText().toString();
            String update = "Cancelled";

            UpdateStat(AStatus, update);
        }
    }
}
