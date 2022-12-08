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

import com.example.ltoappointment.databinding.ActivityLicSpRenewBinding;
import com.example.ltoappointment.databinding.ActivityLicSpUpgradeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class LIC_SP_UPGRADE extends AppCompatActivity implements View.OnClickListener {

    public Uri imageUri1, imageUri2, imageUri3, imageUri4;
    FirebaseStorage storageFile;
    StorageReference storageReference;
    ActivityResultLauncher<String> uf_1, uf_2, uf_3, uf_4;
    ActivityLicSpUpgradeBinding binding;

    StorageReference ref;
    SharedPreferences storage;
    DrawerLayout drawerLayout;
    FirebaseAuth fAuth;
    String userID;

    Button next, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLicSpUpgradeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageFile = FirebaseStorage.getInstance();
        storageFile = FirebaseStorage.getInstance();
        next = findViewById(R.id.nextBTN);
        back = findViewById(R.id.backBTN);
        fAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.sp_upgrade_drawer);

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

        uf_3 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null){
                            binding.uf3.setText(result.getPath());
                            imageUri3 = result;
                        }
                    }
                }
        );
        uf_4 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if(result != null){
                            binding.uf4.setText(result.getPath());
                            imageUri4 = result;
                        }
                    }
                }
        );

        next.setOnClickListener(this);
        back.setOnClickListener(this);
        binding.uf1.setOnClickListener(this);
        binding.uf2.setOnClickListener(this);
        binding.uf3.setOnClickListener(this);
        binding.uf4.setOnClickListener(this);
        binding.uf5.setOnClickListener(this);
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
        startActivity(new Intent(LIC_SP_UPGRADE.this, MainActivity.class));
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

    public void download(){
        storageReference = storageFile.getReference();

        ref = storageReference.child("APLFORM.pdf");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(LIC_SP_UPGRADE.this, "APLFORM", ".pdf", DIRECTORY_DOWNLOADS, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
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

    private void UploadRequirements(){
        StorageReference reference1= storageFile.getReference().child("requirements/" + userID + "/" + UUID.randomUUID());
        StorageReference reference2= storageFile.getReference().child("requirements/" + userID + "/" + UUID.randomUUID());
        StorageReference reference3= storageFile.getReference().child("requirements/" + userID + "/" + UUID.randomUUID());
        StorageReference reference4= storageFile.getReference().child("requirements/" + userID + "/" + UUID.randomUUID());

        if (imageUri1 != null ){
            reference1.putFile(imageUri1)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(LIC_NP_RENEW.this, "upload successful", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LIC_SP_UPGRADE.this, "upload requirement 1 failed", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(LIC_SP_UPGRADE.this, "upload requirement 2 failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "upload requirement 2 first", Toast.LENGTH_SHORT).show();
        }

        if (imageUri3 != null ){
            reference3.putFile(imageUri3)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                // Toast.makeText(LIC_NP_RENEW.this, "upload successful", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LIC_SP_UPGRADE.this, "upload requirement 3 failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "upload requirement 3 first", Toast.LENGTH_SHORT).show();
        }
        if (imageUri4 != null ){
            reference4.putFile(imageUri4)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                // Toast.makeText(LIC_NP_RENEW.this, "upload successful", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LIC_SP_UPGRADE.this, "upload requirement 3 failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "upload requirement 4 first", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.uf1) {
            uf_1.launch("image/*");
        } else if (v.getId() == R.id.uf2) {
            uf_2.launch("image/*");
        } else if (v.getId() == R.id.uf3) {
            uf_3.launch("image/*");
        } else if (v.getId() == R.id.uf4) {
            uf_4.launch("image/*");
        } else if (v.getId() == R.id.uf5) {
            download();
        } else if (v.getId() == R.id.nextBTN) {
            if(imageUri1 == null || imageUri2 == null || imageUri3 == null || imageUri4 == null){
                Toast.makeText(this, "Upload Requirements First", Toast.LENGTH_SHORT).show();
            }
            else{
                UploadRequirements();
                startActivity(new Intent(LIC_SP_UPGRADE.this, Calendar.class));
            }
        }
        else if (v.getId() == R.id.backBTN) {
            startActivity(new Intent(LIC_SP_UPGRADE.this, Homepage.class));
        }
    }
}