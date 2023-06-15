package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class profileActivity extends AppCompatActivity {
EditText mviewusername;
FirebaseAuth firebaseAuth;
FirebaseDatabase firebaseDatabase;
TextView mmovetoupload;
FirebaseFirestore firebaseFirestore;
ImageView mviewUserImageView;
StorageReference storageReference;
FirebaseStorage firebaseStorage;
private String ImageUriAcessTOken;

androidx.appcompat.widget.Toolbar mtoolbarofviewProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mviewUserImageView = findViewById(R.id.viewuserimageimageview);
        mviewusername = findViewById(R.id.viewusername);
        mmovetoupload = findViewById(R.id.movetoupload);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mtoolbarofviewProfile = findViewById(R.id.toolbar_new_profile);
        ImageButton mbackbutton;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mbackbutton = findViewById(R.id.backbuttonofviewprofile);
        setSupportActionBar(mtoolbarofviewProfile);

        mbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("profile pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUriAcessTOken = uri.toString();
                Picasso.get().load(uri).into(mviewUserImageView);
            }
        });
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile muserprofile = snapshot.getValue(userProfile.class);
                mviewusername.setText(muserprofile.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profileActivity.this, "Fail to fetch username", Toast.LENGTH_SHORT).show();
            }
        });
        mmovetoupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profileActivity.this,updateProfileActivity.class);
                intent.putExtra("nameofuser",mviewusername.getText().toString());
                startActivity(intent);
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