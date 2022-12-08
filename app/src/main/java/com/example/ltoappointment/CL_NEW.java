package com.example.ltoappointment;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ltoappointment.databinding.ActivityClNewBinding;
import com.example.ltoappointment.databinding.ActivityLicSpRenewBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class CL_NEW extends AppCompatActivity implements View.OnClickListener{

    public Uri imageUri1, imageUri2;
    FirebaseStorage storageFile;
    StorageReference storageReference;
    ActivityResultLauncher<String> uf_1, uf_2;
    ActivityClNewBinding binding;

    StorageReference ref;
    SharedPreferences storage;
    DrawerLayout drawerLayout;
    FirebaseAuth fAuth;
    String userID;

    Button next, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        next = findViewById(R.id.nextBTN);
        back = findViewById(R.id.backBTN);
        drawerLayout = findViewById(R.id.cl_new_drawer);
        storageFile = FirebaseStorage.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storageFile = FirebaseStorage.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        uf_1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null){
                            binding.uf1.setText(result.getPath());
                            imageUri1 = result;
                        }

                    }
                }
        );
        uf_2 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null){
                            binding.uf2.setText(result.getPath());
                            imageUri2 = result;
                        }
                    }
                }
        );
        binding.uf1.setOnClickListener(this);
        binding.uf2.setOnClickListener(this);
        binding.uf3.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);

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
        startActivity(new Intent(CL_NEW.this, MainActivity.class));
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

    public void downloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url){

        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    public void download(){
        storageReference = storageFile.getReference();

        ref = storageReference.child("APLFORM.pdf");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(CL_NEW.this, "APLFORM", ".pdf", DIRECTORY_DOWNLOADS, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void UploadRequirements(){
        StorageReference reference1= storageFile.getReference().child("requirements/" + userID + "/" + UUID.randomUUID());
        StorageReference reference2= storageFile.getReference().child("requirements/" + userID + "/" + UUID.randomUUID());


        if (imageUri1 != null ){
            reference1.putFile(imageUri1)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(LIC_NP_RENEW.this, "upload successful", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(CL_NEW.this, "upload requirement 1 failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "upload requirement 1 first", Toast.LENGTH_SHORT).show();
        }

        if (imageUri2 != null ){
            reference2.putFile(imageUri2)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(LIC_NP_RENEW.this, "upload successful", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(CL_NEW.this, "upload requirement 2 failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "upload requirement 2 first", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uf1) {
            uf_1.launch("image/*");
        }
        else if (v.getId() == R.id.uf2) {
            uf_2.launch("image/*");
        }
        else if (v.getId() == R.id.uf3) {
            download();
        }
        else if (v.getId() == R.id.nextBTN) {
            if(imageUri1 == null || imageUri2 == null){
                Toast.makeText(this, "Upload Requirements First", Toast.LENGTH_SHORT).show();
            }
            else{
                UploadRequirements();
                startActivity(new Intent(CL_NEW.this, Calendar.class));
            }

        }
        else if (v.getId() == R.id.backBTN) {
            startActivity(new Intent(CL_NEW.this, Homepage.class));
        }
    }
}