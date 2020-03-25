package com.wechat.wechat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.adapters.ContactsAdapter;
import com.wechat.wechat.models.Contact;
import com.wechat.wechat.models.User;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    RecyclerView contact_list_recyclerview;
    LinearLayoutManager linearLayoutManager;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private ArrayList<Contact> contactsList = new  ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contact_list_recyclerview = findViewById(R.id.contact_list_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        contact_list_recyclerview.setLayoutManager(linearLayoutManager);

        Toolbar toolbar = findViewById(R.id.main_activity_contacts_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        startFirebaseConfig();
        getContacs();
    }


    public void getContacs(){
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactsList.clear();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()){
                    User user = objDataSnapshot.getValue(User.class);
                    contactsList.add(new Contact(user.getUsername(),user.getUsername(),user.getEmail(), user.getUserId(), R.drawable.profile, "Online!!" ));

                    ContactsAdapter contactsAdapter = new ContactsAdapter(ContactsActivity.this,contactsList);
                    contact_list_recyclerview.setAdapter(contactsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startFirebaseConfig() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
