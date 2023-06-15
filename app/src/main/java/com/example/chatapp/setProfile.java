package com.example.chatapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setProfile extends AppCompatActivity {
private CardView mgetuserimage;
private ImageView mgetuserimageinimageview;
private static int pick_image = 123;
private Uri imagepath;
private EditText mgetusername;
android.widget.Button msaveprofile;
FirebaseAuth firebaseAuth;
private String name;

private FirebaseStorage firebaseStorage;
private StorageReference storageReference;
private FirebaseFirestore firebaseFirestore;

private String ImageUriAccessToken;
ProgressBar mprogressbartosetprofile;
ActivityResultLauncher<String> mTakePhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
  firebaseAuth = FirebaseAuth.getInstance();
//  firebasestorage is the instance not storing image
  firebaseStorage = FirebaseStorage.getInstance();
//  storage is to store image on basis on fireStorage
  storageReference = firebaseStorage.getReference();
  firebaseFirestore = FirebaseFirestore.getInstance();
  mgetusername = findViewById(R.id.getuserName);
  mgetuserimageinimageview = findViewById(R.id.getuserimageimageview);
  mgetuserimage = findViewById(R.id.getUserImage);
  msaveprofile = findViewById(R.id.saveprofile);
  mprogressbartosetprofile = findViewById(R.id.progressbarofsetprofile);
//new method of picking image
  mTakePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
          new ActivityResultCallback<Uri>() {
              @Override
              public void onActivityResult(Uri result) {
                  imagepath = result;
                mgetuserimageinimageview.setImageURI(result);
              }
          });
        msaveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = mgetusername.getText().toString();

                if(name.isEmpty()){

                    Toast.makeText(setProfile.this,"please Enter UserName",Toast.LENGTH_SHORT).show();

                } else if (imagepath == null) {
                    Toast.makeText(setProfile.this,"Image is Empty",Toast.LENGTH_SHORT).show();

                }
                else{

                    mprogressbartosetprofile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    mprogressbartosetprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(setProfile.this,chatActivity.class);
                    startActivity(intent);

                    finish();
                }

            }
        });
  mgetuserimage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//          Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//          startActivityForResult(intent,pick_image);
          mTakePhoto.launch("image/*");

      }
  });

    }
// old method of picking image does not works
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if(resultCode == pick_image && resultCode == RESULT_OK){
//            imagepath = data.getData();
//            mgetuserimageinimageview.setImageURI(imagepath);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    private void sendDataForNewUser(){
        sendDAtaToRealTimeDatabase();
    }
    private void  sendDAtaToRealTimeDatabase(){
        name = mgetusername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        userProfile userprofile = new userProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(userprofile);
        sendImagetoStorage();
    }
    private  void  sendImagetoStorage()
    {

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
                             ImageUriAccessToken = uri.toString();
                             Toast.makeText(setProfile.this, "URI get Sucess", Toast.LENGTH_SHORT).show();
                             sendDataFirestore();
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(getApplicationContext(), "Uri get failed", Toast.LENGTH_SHORT).show();
                         }
                     });
                     Toast.makeText(getApplicationContext(),"image uploaded",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(setProfile.this, "Image not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendDataFirestore(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        Map<String,Object> userdata = new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","online");
        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data on cloud fire send sucess", Toast.LENGTH_SHORT).show();

            }
        });

    }
}