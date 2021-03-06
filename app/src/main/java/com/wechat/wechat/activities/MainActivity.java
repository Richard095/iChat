package com.wechat.wechat.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.R;
import com.wechat.wechat.adapters.ChatAdapter;
import com.wechat.wechat.helpers.ConverterHelper;
import com.wechat.wechat.helpers.MessageHelper;
import com.wechat.wechat.models.Chat;
import com.wechat.wechat.models.Chats;
import com.wechat.wechat.models.Conversation;
import com.wechat.wechat.models.Invitation;


import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton contactsButton;
    RecyclerView chatListRecycler;
    LinearLayoutManager linearLayoutManager;
    ChatAdapter chatAdapter;
    Toolbar toolbar;
    ImageView defaultImage;
    TextView defaultText;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ArrayList<Chat> chatList = new  ArrayList<>();
    String myUserId;

    private  String nameContact="", usernameId="", urlProfile="";
    ArrayList<Invitation> invitationList = new ArrayList<>();

    private static int  invitationCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        setSupportActionBar(toolbar);

        linearLayoutManager = new LinearLayoutManager(this);
        chatListRecycler.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(this,chatList);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId  = pref.getString("token", null);

        startFirebaseConfiguration();

        fetchMyConversations();
        countMyInvitations();
        
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

        isStoragePermissionGranted();


    }


    public  void bindViews(){
        contactsButton = findViewById(R.id.button_contacts_id);
        chatListRecycler = findViewById(R.id.chats_recyclerview_id);
        toolbar = findViewById(R.id.main_activity_toolbar);
        defaultImage = findViewById(R.id.image_default);
        defaultText = findViewById(R.id.tv_default_text);
    }


    private void startFirebaseConfiguration() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);

        MenuItem notificationItem = menu.findItem(R.id.action_notification);
        notificationItem.setIcon(ConverterHelper.convertLayoutToImage(MainActivity.this,invitationCount,R.drawable.ic_notifications_active_black_24dp));
        MenuItem profile = menu.findItem(R.id.action_acount);
        notificationItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return false;
            }
        });

        profile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return false;
            }
        });

        return true;
    }


    public void openContacts(){
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /** Fetching all conversations*/

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
                            //chatList.add(new Chat("username","message","createdAt","urlProfile","secondUserId","conversationId"));
                            chatList.add(new Chat( nameContact, chats.getPreviewLastMessage(),
                                                   chats.getPreviewLastChatCreatedAt(), urlProfile,
                                                   chats.getUserIdDOS(), chats.getConversationId(),0,myUserId));

                            MessageHelper.modifyCreatedAtToFormatOnChat(chatList);

                        }
                    }

                    if (chatList.size() == 0){
                        defaultImage.setVisibility(View.VISIBLE);
                        defaultText.setVisibility(View.VISIBLE);
                    }else{
                        defaultImage.setVisibility(View.GONE);
                        defaultText.setVisibility(View.GONE);
                    }
                    chatListRecycler.setAdapter(chatAdapter);
                    fetchLastMessage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    /** Getting my invitations */

    public void countMyInvitations() {
        databaseReference.child("User").child(myUserId).child("Invitations")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        invitationList.clear();
                        for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                            Invitation invitation = objDataSnapshot.getValue(Invitation.class);
                            invitationList.add(invitation);
                        }
                        invitationCount = invitationList.size();
                        invalidateOptionsMenu();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public  void fetchLastMessage() {

        for (final Chat chat : chatList) {
            if (chat.getConversationId() != null) {

            databaseReference.child("Conversations")
                    .child(chat.getConversationId()).child("Messages")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int countingMessagesNoRead = 0;
                            ArrayList<Conversation> tempArray = new ArrayList<>();
                            for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                                Conversation conversation = objDataSnapshot.getValue(Conversation.class);
                                if (conversation != null) {
                                    tempArray.add(conversation);
                                    if (!conversation.getSenderId().equals(myUserId)){

                                        if (!conversation.isRead()){
                                            vibrate();
                                            countingMessagesNoRead++;
                                        }
                                    }
                                }
                            }

                            if (tempArray.size() != 0) {

                                MessageHelper.orderMessagesList(tempArray);
                                MessageHelper.modifyCreatedAtToFormat(tempArray);
                                chat.setMessage(tempArray.get(0).getMessage());
                                chat.setCreated_At(tempArray.get(0).getCreated_At());
                                chat.setCountNewMessages(countingMessagesNoRead);
                                chatAdapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors.
                        }
                    });
            }
        }
    }



    // Vibrate for 150 milliseconds
    private  void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }



        public  boolean isStoragePermissionGranted() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v("PERMISION","Permission is granted");
                    return true;
                } else {

                    Log.v("PERMISION","Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return false;
                }
            }
            else { //permission is automatically granted on sdk<23 upon installation
                Log.v("PERMISION","Permission is granted");
                return true;
            }
        }


}
