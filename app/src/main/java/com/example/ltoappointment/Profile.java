package com.example.ltoappointment;

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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {
    FirebaseFirestore db_appointment;
    FirebaseAuth fAuth;
    String currentid;
    DrawerLayout drawerLayout;

    TextView fullname, genderTxt, birthdayTxt, addresstxt, nationalityTxt, emailTxt, numberTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawerLayout = findViewById(R.id.profileDrawer);
        db_appointment = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fullname = findViewById(R.id.fullName);
        genderTxt = findViewById(R.id.gender);
        birthdayTxt = findViewById(R.id.birthday);
        addresstxt = findViewById(R.id.address);
        nationalityTxt = findViewById(R.id.nationality);
        emailTxt = findViewById(R.id.email);
        numberTxt = findViewById(R.id.mobileNumber);

        currentid = fAuth.getCurrentUser().getUid();

        DocumentReference ref = db_appointment.collection("Users").document("Applicant").collection("ApplicantData").document(currentid);
        ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                String Lname = value.getString("user_LN");
                String Fname = value.getString("user_FN");
                String Mname = value.getString("user_MN");
                String b = value.getString("user_B");
                String a = value.getString("user_A");
                String g = value.getString("user_G");
                String n = value.getString("user_N");
                String mn = value.getString("user_CN");
                String e = value.getString("user_E");

                String name = Fname + " " + Mname + " " + Lname;

                fullname.setText(name);
                genderTxt.setText(g);
                birthdayTxt.setText(b);
                addresstxt.setText(a);
                nationalityTxt.setText(n);
                emailTxt.setText(e);
                numberTxt.setText(mn);
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
        startActivity(new Intent(Profile.this, MainActivity.class));
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