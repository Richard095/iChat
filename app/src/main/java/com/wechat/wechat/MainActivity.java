package com.wechat.wechat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.adapters.ChatAdapter;
import com.wechat.wechat.models.Chat;
import com.wechat.wechat.models.Chats;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton contactsButton;
    RecyclerView chatListRecycler;
    LinearLayoutManager linearLayoutManager;
    ChatAdapter chatAdapter;
    Toolbar toolbar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Chat> chatList = new  ArrayList<>();
    String myUserId;

    private  String nameContact="", usernameId="", urlProfile="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsButton = findViewById(R.id.button_contacts_id);
        chatListRecycler = findViewById(R.id.chats_recyclerview_id);
        toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        linearLayoutManager = new LinearLayoutManager(this);
        chatListRecycler.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(this,chatList);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId  = pref.getString("token", null);

        startFirebaseConfiguration();
        fetchMyConversations();

        
        chatAdapter.setOnChatClickListener(new ChatAdapter.OnChatClickListener() {
            @Override
            public void onChatListener(Chat chat) {
                Intent intent = new Intent(MainActivity.this, ChatingActivity.class);
                intent.putExtra("userId", chat.getSecondUserId());
                intent.putExtra("username", chat.getUsername());
                intent.putExtra("conversationId", chat.getConversationId());
                startActivity(intent);
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContacts();
            }
        });
    }



    private void startFirebaseConfiguration() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }


    public void openContacts(){
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    public void fetchMyConversations() {
        if (myUserId != null) {
            databaseReference.child("User").child(myUserId).child("Conversations").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatList.clear();
                    for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                        Chats chats = objDataSnapshot.getValue(Chats.class);

                        if (chats != null) {
                            if (myUserId.equals(chats.getUserIdDOS())) {
                                usernameId = chats.getUserIdUno();
                                nameContact = chats.getUserNameUno();
                                urlProfile = chats.getProfileUrlUno();

                            } else if (myUserId.equals(chats.getUserIdUno())) {
                                usernameId = chats.getUserIdDOS();
                                nameContact = chats.getGetUserNameDos();
                                urlProfile = chats.getProfileUrlDos();
                            }

                            chatList.add(new Chat( nameContact, chats.getPreviewLastMessage(),
                                                   usernameId, urlProfile, chats.getUserIdUno(),
                                                   chats.getUserIdDOS(), chats.getConversationId()));
                        }
                    }
                    chatListRecycler.setAdapter(chatAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


}
