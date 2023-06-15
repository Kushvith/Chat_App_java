package com.example.chatapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class updateProfileActivity extends AppCompatActivity {
    EditText mupdateusername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
   android.widget.Button mupdatebtn;
    FirebaseFirestore firebaseFirestore;
    ImageView mupdateUserImageView;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    androidx.appcompat.widget.Toolbar mtoolbarofupdateuser;
    ImageButton mbackbuttonofupdateprofile;
    String ImageAccessToken;
    ProgressBar mprogressbarofupdateprofile;
    private Uri imagepath;


    ActivityResultLauncher<String> mTakePhoto;
    Intent intent;
    private static int Pick_Image = 123;
    String newname;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mtoolbarofupdateuser = findViewById(R.id.toolbar_update_profile);
        mbackbuttonofupdateprofile = findViewById(R.id.backbuttonofupdateprofile);
        mupdateUserImageView = findViewById(R.id.updateuserimageimageview);
        mprogressbarofupdateprofile = findViewById(R.id.progressbarofupdateprofile);
        mupdateusername = findViewById(R.id.updateusername);
        mupdatebtn = findViewById(R.id.updateprofilebutton);
        firebaseAuth = FirebaseAuth.getInstance();
//        this is for realtime firebase is used for database reference
        firebaseDatabase = FirebaseDatabase.getInstance();
//        this is for the storage refernce
        firebaseStorage = FirebaseStorage.getInstance();

//        store is to store the final value
        firebaseFirestore = FirebaseFirestore.getInstance();
        intent = getIntent();
        mupdateusername.setText(intent.getStringExtra("nameofuser"));
        setSupportActionBar(mtoolbarofupdateuser);
        mTakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imagepath = result;
                        mupdateUserImageView.setImageURI(result);
                    }
                });
        mbackbuttonofupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mupdateUserImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//          Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//          startActivityForResult(intent,pick_image);
                mTakePhoto.launch("image/*");

            }
        });
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        mupdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newname = mupdateusername.getText().toString();
                if(newname.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name is empty",Toast.LENGTH_SHORT).show();
                } else if (imagepath!= null) {
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userProfile userProfile = new userProfile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(userProfile);
                    updateImagetostorage();
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(updateProfileActivity.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userProfile userProfile = new userProfile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(userProfile);
                    updatenameoncloudfirestore();
                    Toast.makeText(getApplicationContext(),"upadated",Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(updateProfileActivity.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("profile pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageAccessToken = uri.toString();
                Picasso.get().load(uri).into(mupdateUserImageView);

            }
        });

    }

    private void updatenameoncloudfirestore() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        Map<String,Object> userdata = new HashMap<>();
        userdata.put("name",newname);
        userdata.put("image",ImageAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","online");
        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data on cloud fire send sucess", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateImagetostorage() {
        StorageReference imgref = storageReference.child("Images").child(firebaseAuth.getUid()).child("profile pic");
        //        imge compression
        Bitmap bitmap =  null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        // putting image in storage
        UploadTask uploadTask = imgref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageAccessToken = uri.toString();
                        Toast.makeText(updateProfileActivity.this, "URI get Sucess", Toast.LENGTH_SHORT).show();
                        updatenameoncloudfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Uri get failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(),"image updated",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(updateProfileActivity.this, "Image not updated", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        documentReference.update("status","offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"now user is offline",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        documentReference.update("status","online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"now user is online",Toast.LENGTH_SHORT).show();
            }
        });
    }

}