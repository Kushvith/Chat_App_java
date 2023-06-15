package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class chatActivity extends AppCompatActivity {
TabLayout tabLayout;
TabItem mchat,mcall,mstatus;
ViewPager viewPager;
PagerAdapter pagerAdapter;
FirebaseFirestore firebaseFirestore;
FirebaseAuth firebaseAuth;
androidx.appcompat.widget.Toolbar mtoolbar;

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

           if(item.getItemId()== R.id.profile) {
               Intent intent = new Intent(chatActivity.this, profileActivity.class);
               startActivity(intent);
           } else if (item.getItemId() == R.id.settings) {
               Toast.makeText(this, "setting clicked", Toast.LENGTH_SHORT).show();
           }

        return  true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tabLayout = findViewById(R.id.include);
        mchat = findViewById(R.id.chat);
        mstatus = findViewById(R.id.status);
        mcall = findViewById(R.id.calls);
        mtoolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.fragmentcontainer);
        setSupportActionBar(mtoolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_more_vert_24);
        pagerAdapter = new pagerAdopter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0||tab.getPosition()==1||tab.getPosition()==2){
                    pagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
       viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//       for menu items

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