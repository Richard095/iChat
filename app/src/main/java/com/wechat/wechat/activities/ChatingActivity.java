package com.wechat.wechat.activities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wechat.wechat.R;
import com.wechat.wechat.adapters.ConversationAdapter;
import com.wechat.wechat.helpers.ImageHelper;
import com.wechat.wechat.helpers.MessageHelper;
import com.wechat.wechat.models.Chat;
import com.wechat.wechat.models.Chats;
import com.wechat.wechat.models.Conversation;
import com.wechat.wechat.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;



public class ChatingActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    FloatingActionButton sendMessageButton;
    RecyclerView messagesRecyclerview;
    TextView toolbarTitle, typingInput;
    Toolbar toolbar;

    ImageView imageSendMessage;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ConversationAdapter conversationAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Conversation> conversationList = new  ArrayList<>();

    String myUserId, contactUserId, contactUsername,conversationId, contactUrlProfile;
    String typeMessage, imageUniqueId, tempMessage = "";

    FirebaseStorage storage;
    StorageReference storageReference;

    Bitmap bitmap;
    Uri uriImage;
    String urlImageReceived, senderImageUrl;

    ValueEventListener messageListener=null;

    ArrayList<Conversation> tempArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        bindViews();

        linearLayoutManager = new LinearLayoutManager(ChatingActivity.this);
        linearLayoutManager.setReverseLayout(true);
        conversationAdapter = new ConversationAdapter(ChatingActivity.this,conversationList);
        messagesRecyclerview.setLayoutManager(linearLayoutManager);

        gettingExtrasFromParentActivity();

        toolbarTitle.setText(contactUsername);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId  = pref.getString("token", null);

        startButtonEvents();

        toolbarConfigurations();
        startFirebaseConfigurations();
        getMessages();

        conversationAdapter.setOnConversationClickListener(new ConversationAdapter.OnConversationClickListener() {
            @Override
            public void onOpenImageClickListener(Conversation conversation) {
                Intent intent = new Intent(ChatingActivity.this, ImageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("urlImage", conversation.getUrlImage());
                startActivity(intent);
            }

        });

    }



    public void bindViews(){
        sendMessageButton = findViewById(R.id.send_message_button_id);
        typingInput = findViewById(R.id.et_message_to_send);
        toolbar = findViewById(R.id.activit_chating_toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title_chating);
        messagesRecyclerview= findViewById(R.id.recyclerview_chating_id);
        imageSendMessage = findViewById(R.id.iv_sendImage);
    }


    public void gettingExtrasFromParentActivity(){
        contactUserId = getIntent().getStringExtra("userId");
        contactUsername = getIntent().getStringExtra("username");
        conversationId = getIntent().getStringExtra("conversationId");
        contactUrlProfile = getIntent().getStringExtra("contactUrlProfile");

        //Toast.makeText(this,conversationId , Toast.LENGTH_SHORT).show();
    }


    public void startButtonEvents(){

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeMessage = "TEXT";
                if (!typingInput.getText().toString().isEmpty()) {
                    StartConversationWithMyContact();
                }
            }
        });

        imageSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeMessage = "IMAGE";
                chooseImageAndSend();
            }
        });

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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        final String createdAt = df.format(Calendar.getInstance().getTime());

        if (conversationId.equals("")){
            conversationId =  UUID.randomUUID().toString();

            saveConversation(contactUsername,"Mensaje provicinal :(", createdAt, contactUserId, conversationId);getMessages();
            databaseReference.child("User").child(myUserId).child("Contacts").child(contactUserId).child("conversationId").setValue(conversationId);
            databaseReference.child("User").child(contactUserId).child("Contacts").child(myUserId).child("conversationId").setValue(conversationId);

        }else{
            sendMessage(conversationId);
        }


    }

    public void saveConversation(final String username, final String message, final String createdAt,  final String secondUserId, final String conversationId){
        Chat chat = new Chat(username,message,createdAt,"", secondUserId, conversationId, 0,myUserId);
        databaseReference.child("Conversations").child(conversationId).setValue(chat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendMessage(conversationId);
                        saveMyContactsWithChatActive(conversationId);
                    }
                });
    }

    /** Saving conversation for each user  MyUser &&* MyUserContact */

    public void saveMyContactsWithChatActive(final String conversationId){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        final String createdAt = df.format(Calendar.getInstance().getTime());

            databaseReference.child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                        User user = objDataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (myUserId != null){
                                if (myUserId.equals(user.getUserId())){
                                    Chats chats = new Chats( tempMessage, createdAt, conversationId, myUserId, contactUserId, user.getFullname(), contactUsername, user.getUrlProfile(), contactUrlProfile);
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


    /** Sending message Type TEXT || IMAGE */

    public void sendMessage(final String conversationId){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        final String createdAt = df.format(Calendar.getInstance().getTime());
        tempMessage = typingInput.getText().toString();
        final String message = typingInput.getText().toString();
        typingInput.setText("");

        if (typeMessage.equals("TEXT")){
            String uniqueID = UUID.randomUUID().toString();
            Conversation conversation = new Conversation(message, createdAt, "NO_image",typeMessage,conversationId, myUserId, false, uniqueID,"NO_IMAGE");

            databaseReference.child("Conversations").child(conversationId).child("Messages").child(uniqueID).setValue(conversation);

        }else if(typeMessage.equals("IMAGE")){
            String uniqueID = UUID.randomUUID().toString();
            Conversation conversation = new Conversation(typingInput.getText().toString(), createdAt, "NO_READY",typeMessage,conversationId, myUserId, false,uniqueID, senderImageUrl);
            imageUniqueId = uniqueID;
            databaseReference.child("Conversations").child(conversationId).child("Messages").child(uniqueID).setValue(conversation);
        }

    }


    /** Getting all messages from chat */

    public void getMessages() {
        if (conversationId != null) {
               messageListener = databaseReference.child("Conversations")
                        .child(conversationId)
                        .child("Messages")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                conversationList.clear();

                                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                                    Conversation conversation = objDataSnapshot.getValue(Conversation.class);
                                    if (conversation != null){
                                        conversationList.add(conversation);
                                        if ( !conversation.getSenderId().equals(myUserId) ){
                                            if (!conversation.isRead()){
                                                if (conversation.getMessageId() != null){
                                                    databaseReference.child("Conversations").child(conversationId).child("Messages").child(conversation.getMessageId()).child("read").setValue(true);
                                                }
                                            }
                                        }

                                        /** Processing image :::::::::::::::::
                                        if (!conversation.getSenderId().equals(myUserId)){
                                            if (conversation.getMessageType().equals("IMAGE")){
                                                if (conversation.getUrlImage().startsWith("https://")){
                                                    fetchImageOnBackgroundProcess(conversation.getUrlImage(),conversation.getConversationId(),conversation.getMessageId());
                                                }
                                            }
                                        }
                                        ::::::::::::::::::::::::::::::::::::::::::::*/

                                    }
                                }



                                MessageHelper.orderMessagesList(conversationList);
                                messagesRecyclerview.setAdapter(conversationAdapter);
                                MessageHelper.modifyCreatedAtToFormat(conversationList);
                                messagesRecyclerview.smoothScrollToPosition(-2);




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }

                        });

        }
    }

    /** Select and Upload image */

    public void chooseImageAndSend(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage  = data.getData();
            try {
                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);

                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 Bitmap bitmapPreparedSaved = bitmap;
                 bitmapPreparedSaved.compress(Bitmap.CompressFormat.JPEG,30,stream);
                 byte[] byteArray = stream.toByteArray();
                 Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

                 senderImageUrl = ImageHelper.saveImage(compressedBitmap, ChatingActivity.this);
                 urlImageReceived = senderImageUrl;
                 StartConversationWithMyContact();
                 uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void uploadImage(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] data = baos.toByteArray();
        final String imageId = UUID.randomUUID().toString();

        StorageReference reference = storageReference.child("messageImages/"+ imageId);
        final  UploadTask uploadTask = reference.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ChatingActivity.this, "Has enviado una imagen", Toast.LENGTH_SHORT).show();

                final StorageReference ref = storageReference.child("messageImages/"+ imageId);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            if (task.getException() != null ) throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            if (downloadUri != null){
                                databaseReference.child("Conversations").child(conversationId).child("Messages").child(imageUniqueId).child("urlImage").setValue(downloadUri.toString());
                                //Toast.makeText(ChatingActivity.this, "Updated ", Toast.LENGTH_SHORT).show();
                                imageUniqueId ="";
                            }
                        } else {
                            Toast.makeText(ChatingActivity.this, "Hubo un problema con la descarga.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null)
            databaseReference.child("Conversations").child(conversationId).child("Messages").removeEventListener(messageListener);
    }


    public void fetchImageOnBackgroundProcess(final String urlImage,final  String conversationId,final  String messageId){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageHelper.getBitmapFromURL(urlImage);
                if (bitmap != null) {
                    String newImageUrl = ImageHelper.saveImage(bitmap, ChatingActivity.this);
                    databaseReference.child("Conversations")
                            .child(conversationId)
                            .child("Messages")
                            .child(messageId)
                            .child("urlImage").setValue(newImageUrl);
                }
            }
        });



    }

}
