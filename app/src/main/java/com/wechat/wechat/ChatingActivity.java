package com.wechat.wechat;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.adapters.ConversationAdapter;
import com.wechat.wechat.models.Chat;
import com.wechat.wechat.models.Chats;
import com.wechat.wechat.models.Conversation;
import com.wechat.wechat.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class ChatingActivity extends AppCompatActivity {

    FloatingActionButton sendMessageButton;
    RecyclerView messagesRecyclerview;
    TextView toolbarTitle;

    EditText typingInput;
    Toolbar toolbar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ConversationAdapter conversationAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Conversation> conversationList = new  ArrayList<>();
    String myUserId, contactUserId, contactUsername, conversationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        sendMessageButton = findViewById(R.id.send_message_button_id);
        typingInput = findViewById(R.id.et_message_to_send);
        toolbar = findViewById(R.id.activit_chating_toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title_chating);
        messagesRecyclerview= findViewById(R.id.recyclerview_chating_id);


        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        messagesRecyclerview.setLayoutManager(linearLayoutManager);
        conversationAdapter = new ConversationAdapter(this,conversationList);

        contactUserId = getIntent().getStringExtra("userId");
        contactUsername = getIntent().getStringExtra("username");
        conversationId = getIntent().getStringExtra("conversationId");
        toolbarTitle.setText(contactUsername);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        myUserId  = pref.getString("token", null);


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartConversationWithMyContact();
            }
        });



        toolbarConfigurations();
        startFirebaseConfigurations();
        getMessages();

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

    public void toolbarConfigurations(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    public void StartConversationWithMyContact(){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        final String createdAt = df.format(Calendar.getInstance().getTime());
        if (conversationId.equals("")){
            String uniqueID = UUID.randomUUID().toString();
            conversationId = uniqueID;
            saveConversation(contactUsername,"Mensaje provicinal :(", createdAt,myUserId, contactUserId, uniqueID );
            getMessages();
        }else{
            sendMessage(conversationId);
        }
    }


    public void saveConversation(final String username, final String message, final String createdAt, final String firstUserId, final String secondUserId, final String conversationId){
        Chat chat = new Chat(username,message,createdAt,0,firstUserId, secondUserId, conversationId);
        databaseReference.child("Conversations").child(conversationId).setValue(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendMessage(conversationId);
                        saveMyContactsWithChatActive(conversationId);
                    }
                });

    }


    public void saveMyContactsWithChatActive(final String conversationId){


            databaseReference.child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                        User user = objDataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (myUserId != null){
                                if (myUserId.equals(user.getUserId())){
                                    Chats chats = new Chats(
                                            "Mensaje provicional",
                                            "Timepstamp provicional",
                                            "",
                                            conversationId,
                                            myUserId,
                                            contactUserId,
                                            user.getFullname(),
                                            contactUsername);

                                    databaseReference.child("User").child(myUserId).child("Conversations").child(conversationId).setValue(chats);
                                    databaseReference.child("User").child(contactUserId).child("Conversations").child(conversationId).setValue(chats);


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


    public void sendMessage(String conversationId){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String createdAt = df.format(Calendar.getInstance().getTime());
        String message = typingInput.getText().toString();
        Conversation conversation = new Conversation(message, createdAt, "NO_image",conversationId);
        String uniqueID = UUID.randomUUID().toString();
        typingInput.setText("");
        databaseReference.child("Conversations").child(conversationId).child("Messages").child(uniqueID).setValue(conversation);
    }

    public void getMessages(){
        databaseReference.child("Conversations").child(conversationId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conversationList.clear();
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()){
                    Conversation conversation = objDataSnapshot.getValue(Conversation.class);
                    conversationList.add(conversation);
                }
                messagesRecyclerview.setAdapter(conversationAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





}
