package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SpecificChatActivity extends AppCompatActivity {
    EditText mgetmessage;
    ImageView mmessgebutton;
    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtooolbarofspecificchat;
    ImageView mimageofuser;
    TextView mnameofspecificuser;
    String enteredmessage;
    Intent intent;
    String mrecievername, sendername, mreciveruid, msenderuid;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    String senderroom, reciverroom;

    ImageButton mbackbuttonofspecificchat;
    RecyclerView mmessagerecyclerview;
    String currentime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    MessagesAdopter messagesAdopter;
    ArrayList<Messages> messagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);
        mgetmessage = findViewById(R.id.getmessage);
        msendmessagecardview = findViewById(R.id.cardviewofsendbtn);
        mmessgebutton = findViewById(R.id.imageviewofsendmsg);
        mtooolbarofspecificchat = findViewById(R.id.toolbar_specific_chat);
        mnameofspecificuser = findViewById(R.id.myapptext);
        mimageofuser = findViewById(R.id.specificuserimageimageview);
        mbackbuttonofspecificchat = findViewById(R.id.backbuttonofspecificchat);
        intent = getIntent();
        calendar = Calendar.getInstance();
        setSupportActionBar(mtooolbarofspecificchat);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        msenderuid = firebaseAuth.getUid();
        mreciveruid = intent.getStringExtra("reciveruid");
        mrecievername = intent.getStringExtra("name");
        senderroom = msenderuid + mreciveruid;
        reciverroom = mreciveruid + msenderuid;
        messagesArrayList = new ArrayList<>();
        mmessagerecyclerview = findViewById(R.id.recyclerofspecificchat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdopter = new MessagesAdopter(SpecificChatActivity.this,messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdopter);
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats")
                .child(senderroom)
                        .child("messages");

        messagesAdopter = new MessagesAdopter(SpecificChatActivity.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdopter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //mimageofuser.setImageURI(Uri.parse(intent.getStringExtra("imageuri"))););
        mtooolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "toolbar clicked", Toast.LENGTH_SHORT).show();

            }
        });
        mbackbuttonofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mnameofspecificuser.setText(mrecievername);
        String uri = intent.getStringExtra("imageuri");
        if (uri.isEmpty()) {
            Toast.makeText(getApplicationContext(), "image not recived", Toast.LENGTH_SHORT).show();

        } else {
            Picasso.get().load(uri).into(mimageofuser);
        }

        mmessgebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredmessage = mgetmessage.getText().toString();
                if (enteredmessage.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter the message first", Toast.LENGTH_SHORT).show();

                } else {
                    Date date = new Date();
                    currentime = simpleDateFormat.format(calendar.getTime());
                    Messages messages = new Messages(enteredmessage, firebaseAuth.getUid(), date.getTime(), currentime);
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderroom)
                            .child("messages")
                            .push()
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseDatabase.getReference().child("chats")
                                            .child(reciverroom)
                                            .child("messages")
                                            .push()
                                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(),"message sent",Toast.LENGTH_SHORT).show();
                                                    mgetmessage.setText(null);
                                                }
                                            });

                                }
                            });


                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdopter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(messagesAdopter!=null){
            messagesAdopter.notifyDataSetChanged();
        }
    }
}