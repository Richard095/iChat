package com.wechat.wechat.activities;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.R;
import com.wechat.wechat.adapters.ContactsAdapter;
import com.wechat.wechat.models.Chat;
import com.wechat.wechat.models.Contact;
import com.wechat.wechat.models.Invitation;
import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    LinearLayoutManager linearLayoutManager;
    RecyclerView contactListRecyclerview;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    ContactsAdapter contactsAdapter;

    ArrayList<Contact> contactsList = new  ArrayList<>();
    ArrayList<Invitation> tempContactList = new ArrayList<>();
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
                StartChatWithContact(contact.getUserId(), contact.getUsername(), contact.getConversationId(), contact.getProfileUrl());
            }
        });

        startFirebaseConfigurations();
        fetchMyContacts();

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

    public void StartChatWithContact(String contactUserId, String userName, String conversationId, String contactrUrlProfile){
        Intent chatIntent = new Intent(ContactsActivity.this, ChatingActivity.class);
        chatIntent.putExtra("userId", contactUserId);
        chatIntent.putExtra("username", userName);
        chatIntent.putExtra("conversationId", conversationId);
        chatIntent.putExtra("contactUrlProfile", contactrUrlProfile);
        startActivity(chatIntent);
        finish();
    }



    /** checking if there is an active conversation with any of the contacts on contactsList*/
    public void CheckIfExistConversationOnContact(final String secondUserId) {
        if (secondUserId != null) {
                databaseReference.child("Conversations").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                            Chat chat = objDataSnapshot.getValue(Chat.class);
                            if (chat != null) {
                                if (chat.getSecondUserId() != null){
                                    if (chat.getSecondUserId().equals(secondUserId)) {
                                        for (int i = 0; i < contactsList.size(); i++) {
                                            if (contactsList.get(i).getUserId().equals(chat.getSecondUserId())) {
                                                contactsList.get(i).setConversationId(chat.getConversationId());
                                            }
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


    /** Fetching  list contacts*/

    public void fetchMyContacts(){
        databaseReference.child("User").child(myUserId).child("Contacts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tempContactList.clear();
                        contactsList.clear();
                        for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                            Invitation invitation = objDataSnapshot.getValue(Invitation.class);
                            tempContactList.add(invitation);
                        }
                        for ( Invitation invitation : tempContactList ){
                            getDataForEachContact(invitation.getUserId());
                            CheckIfExistConversationOnContact(invitation.getUserId());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /** Getting data for each contact in my list*/

    public void getDataForEachContact(final String userId) {
        databaseReference.child("User")
                .orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            String username = childDataSnapshot.child("username").getValue().toString();
                            String email = childDataSnapshot.child("email").getValue().toString();
                            String userId = childDataSnapshot.child("userId").getValue().toString();
                            String profileUrl = childDataSnapshot.child("urlProfile").getValue().toString();
                            contactsList.add(new Contact(username, username, email, userId, profileUrl, "Online", ""));
                        }
                        contactListRecyclerview.setAdapter(contactsAdapter);


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
