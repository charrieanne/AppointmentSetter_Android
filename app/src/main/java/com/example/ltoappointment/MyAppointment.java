package com.example.ltoappointment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MyAppointment extends AppCompatActivity {
    private FirebaseFirestore db_appointment;
    FirebaseAuth fAuth;
    String currentid;
    DrawerLayout drawerLayout;
    TableLayout appointment;

    TextView approved, name, transaction, applicationDate, appointmentDate, status;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointment);

        drawerLayout = findViewById(R.id.MyAppointmentDrawer);
        db_appointment = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.fullName);
        transaction = findViewById(R.id.transactionType);
        applicationDate = findViewById(R.id.dateApplication);
        appointmentDate = findViewById(R.id.dateAppointment);
        status = findViewById(R.id.applicationStatus);

        approved = findViewById(R.id.noAppointment);
        cancel = findViewById(R.id.cancelBTN);
        appointment = findViewById(R.id.appointmentTBL);

        currentid = fAuth.getCurrentUser().getUid();

        db_appointment.collection("Appointment")
                .whereEqualTo("user_ID", currentid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot doc : value){
                            if(doc.get("A_Stat").equals("Approved") ){
                                appointment.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                approved.setVisibility(View.INVISIBLE);

                                transaction.setText(doc.getString("A_tt"));
                                applicationDate.setText(doc.getString("A_dateApplication"));
                                appointmentDate.setText(doc.getString("A_dateAppointment"));
                                status.setText(doc.getString("A_Stat"));
                            }
                            else if(doc.get("A_Stat").equals("Cancelled")){
                                approved.setVisibility(View.VISIBLE);
                            }
                            else{
                                Toast.makeText(MyAppointment.this, "Set an Appointment with LTO", Toast.LENGTH_SHORT).show();
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approved.setVisibility(View.VISIBLE);
                appointment.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);

                String AStatus = status.getText().toString();
                String update = "Cancelled";

                UpdateStat(AStatus, update);
            }
        });
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
                                            Toast.makeText(MyAppointment.this, "Your appointment for has been cancelled", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MyAppointment.this, "Sorry, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(MyAppointment.this, "Cancellation Incomplete", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(MyAppointment.this, MainActivity.class));
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
}