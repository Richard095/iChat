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
import com.wechat.wechat.R;
import com.wechat.wechat.adapters.Utils;

public class ProfileActivity extends AppCompatActivity {

    ImageView iv_thumb;
    TextView tv_status_profile,tv_username_profile;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String myUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        iv_thumb = findViewById(R.id.iv_thumb);
        tv_status_profile = findViewById(R.id.tv_status_profile);
        tv_username_profile = findViewById(R.id.tv_username_profile);
        Toolbar toolbar = findViewById(R.id.main_activity_profile_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId = pref.getString("token", null);

        startFirebaseConfigurations();
        gettingMyData();
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
                            tv_username_profile.setText(myUsername);
                            Utils.fetchSvg(ProfileActivity.this, urlProfile, iv_thumb);
                            /**
                            Picasso.get()
                                    .load(urlProfile)
                                    .resize(500,500)
                                    .centerCrop()
                                    .placeholder(R.drawable.waiting)
                                    .into(iv_thumb);

                             */
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
