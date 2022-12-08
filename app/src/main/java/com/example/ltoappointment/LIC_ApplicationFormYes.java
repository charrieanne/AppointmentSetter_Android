package com.example.ltoappointment;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.ltoappointment.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class LIC_ApplicationFormYes extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    Spinner licenseType;
    Button next, back, uf1, uf2, uf3, uf4, uf5, uf6, uf7, uf8, uf9, uf10, uf11, uf12, uf13;
    TableLayout p_renew, np_upgrade, np_renew;
    ActivityResultLauncher<String> uploadPhoto;

    public Uri imageUri;
    private FirebaseStorage storageFile;
    StorageReference storageReference;
    SharedPreferences storage;

    public static final String SHARED_PREF = "shared preferences";
    public static final String Transaction = "transaction_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lic_application_form_yes);

        storageFile = FirebaseStorage.getInstance();
        storageReference = storageFile.getReference();
        uploadPhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {;
                        uf1.setText(result.getPath());
                    }
                });

        licenseType = findViewById(R.id.license_type);
        next = findViewById(R.id.nextBtn);
        back = findViewById(R.id.backBtn);
        p_renew = findViewById(R.id.requirements_p_renew);
        np_renew = findViewById(R.id.requirements_np_renew);
        np_upgrade = findViewById(R.id.requirements_np_upgrade);

        uf1 = findViewById(R.id.uf_1);
        uf2 = findViewById(R.id.uf_2);
        uf3 = findViewById(R.id.uf_3);
        uf4 = findViewById(R.id.uf_4);
        uf5 = findViewById(R.id.uf_5);
        uf6 = findViewById(R.id.uf_6);
        uf7 = findViewById(R.id.uf_7);
        uf8 = findViewById(R.id.uf_8);
        uf9 = findViewById(R.id.uf_9);
        uf10 = findViewById(R.id.uf_10);
        uf11 = findViewById(R.id.uf_11);
        uf12 = findViewById(R.id.uf_12);
        uf13 = findViewById(R.id.uf_13);


        //local storage to cache data
        storage = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        String[] licensetype = new String[] {" ", "Driver's License Non-Professional Renewal", "Driver's License Non-Professional Upgrade (Professional)", "Driver's License Professional Renew" };

        ArrayAdapter<String> license = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, licensetype);
        license.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        licenseType.setAdapter(license);
        licenseType.setOnItemSelectedListener(this);

        next.setOnClickListener(this);
        back.setOnClickListener(this);
        uf1.setOnClickListener(this);
        uf2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        String LicenseType = licenseType.getSelectedItem().toString();

        if(v.getId() == R.id.nextBtn){
            if(LicenseType.equals(" ")){
                Toast.makeText(this, "Please fill out application", Toast.LENGTH_SHORT).show();
            }
            else{
                //storing data or input
                SharedPreferences.Editor editor = storage.edit();
                editor.putString("license_type", LicenseType);
                editor.apply();
                startActivity(new Intent(LIC_ApplicationFormYes.this, Calendar.class));
            }
        }
        else if(v.getId() == R.id.backBtn){
            startActivity(new Intent(LIC_ApplicationFormYes.this, Homepage.class));
        }
        else if(v.getId() == R.id.uf_1){
            uploadPhoto.launch("image/jpg");
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String LicenseType = licenseType.getSelectedItem().toString();

        //condition for non-pro renewal
        if(LicenseType.equals("Driver's License Non-Professional Renewal")){
            np_renew.setVisibility(View.VISIBLE);
        }
        else{
            np_renew.setVisibility(View.INVISIBLE);
        }

        //condition for non-pro upgrade
        if (LicenseType.equals("Driver's License Non-Professional Upgrade (Professional)")){
            np_upgrade.setVisibility(View.VISIBLE);
        }
        else {
            np_upgrade.setVisibility(View.INVISIBLE);
        }

        //condition for pro renewal
        if (LicenseType.equals("Driver's License Professional Renew")){
            p_renew.setVisibility(View.VISIBLE);
        }
        else {
            p_renew.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void uploadFile(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Image Uploading...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progressPercent + "%");
                    }
                });
    }
}