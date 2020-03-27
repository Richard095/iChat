package com.wechat.wechat;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.wechat.wechat.models.Chat;
import com.wechat.wechat.models.Contact;
import com.wechat.wechat.models.User;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    RecyclerView contactListRecyclerview;
    LinearLayoutManager linearLayoutManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ContactsAdapter contactsAdapter;

    ArrayList<Contact> contactsList = new  ArrayList<>();
    String  myUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactListRecyclerview = findViewById(R.id.contact_list_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        contactListRecyclerview.setLayoutManager(linearLayoutManager);
        contactsAdapter = new ContactsAdapter(ContactsActivity.this,contactsList);

        Toolbar toolbar = findViewById(R.id.main_activity_contacts_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        myUserId  = pref.getString("token", null);




        contactsAdapter.setOnContactClickListener(new ContactsAdapter.OnContactClickListener() {
            @Override
            public void onContactListener(Contact contact) {
                StartChatWithContact(contact.getUserId(), contact.getUsername(), contact.getConversationId() );
            }
        });
        startFirebaseConfigurations();
        gettingAllMyContacts();


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


    private void startFirebaseConfigurations() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void gettingAllMyContacts(){
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contactsList.clear();

                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {

                    User user = objDataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (myUserId != null) {
                            if (!myUserId.equals(user.getUserId())) {
                                CheckIfExistConversationOnContact(user.getUserId());
                                contactsList.add(new Contact(user.getUsername(), user.getUsername(), user.getEmail(), user.getUserId(), R.drawable.profile, "Online!!", ""));
                                contactListRecyclerview.setAdapter(contactsAdapter);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void StartChatWithContact(String contactUserId, String userName, String conversationId){
        Intent chatIntent = new Intent(ContactsActivity.this, ChatingActivity.class);
        chatIntent.putExtra("userId", contactUserId);
        chatIntent.putExtra("username", userName);
        chatIntent.putExtra("conversationId", conversationId);
        startActivity(chatIntent);
        finish();
    }


    public void CheckIfExistConversationOnContact(final String secondUserId){
        databaseReference.child("Conversations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()){
                    Chat chat = objDataSnapshot.getValue(Chat.class);
                    if (chat !=null ){
                        if (chat.getSecondUserId().equals(secondUserId)){
                            for (int i=0 ; i < contactsList.size(); i++){
                                if (contactsList.get(i).getUserId().equals(chat.getSecondUserId())){
                                    contactsList.get(i).setConversationId(chat.getConversationId());
                                }
                            }
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



}
