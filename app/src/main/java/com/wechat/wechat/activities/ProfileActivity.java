package com.wechat.wechat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wechat.wechat.R;

public class ProfileActivity extends AppCompatActivity {

    ImageView thumbProfileImage;
    TextView profileTextView,usernameProfileText, emailProfileText;
    Toolbar toolbar;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String myUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        bindViews();

        toolbarConfigurations();


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId = pref.getString("token", null);

        startFirebaseConfigurations();
        gettingMyData();
    }

    /** Binding views from xml file */

    public void bindViews(){
        thumbProfileImage = findViewById(R.id.iv_thumb);
        profileTextView = findViewById(R.id.tv_status_profile);
        usernameProfileText = findViewById(R.id.tv_username_profile);
        toolbar = findViewById(R.id.main_activity_profile_toolbar);
        emailProfileText = findViewById(R.id.tv_email_profile);
    }

    /** Toolbar configuration */

    public void toolbarConfigurations(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }



    private void startFirebaseConfigurations() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void gettingMyData() {
        databaseReference.child("User")
                .orderByChild("userId").equalTo(myUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            String  myUsername = childDataSnapshot.child("username").getValue().toString();
                            String  urlProfile = childDataSnapshot.child("urlProfile").getValue().toString();
                            String  email = childDataSnapshot.child("email").getValue().toString();
                            usernameProfileText.setText(myUsername);
                            emailProfileText.setText(email);
                            //Utils.fetchSvg(ProfileActivity.this, urlProfile, thumbProfileImage);

                            Picasso.get()
                                    .load(urlProfile)
                                    .resize(500,500)
                                    .centerCrop()
                                    .placeholder(R.drawable.asset_waiting)
                                    .into(thumbProfileImage);


                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
