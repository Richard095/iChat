package com.wechat.wechat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.wechat.wechat.adapters.ChatAdapter;
import com.wechat.wechat.adapters.ConversationAdapter;
import com.wechat.wechat.models.Chat;
import com.wechat.wechat.models.Chats;
import com.wechat.wechat.models.Conversation;
import com.wechat.wechat.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;



public class ChatingActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    FloatingActionButton sendMessageButton;
    RecyclerView messagesRecyclerview;
    TextView toolbarTitle;

    EditText typingInput;
    Toolbar toolbar;
    ImageView imageSendMessage;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ConversationAdapter conversationAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Conversation> conversationList = new  ArrayList<>();
    String myUserId, contactUserId, contactUsername,conversationId, contactUrlProfile;
    String typeMessage;
    String message = "";

    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap;
    Uri uriImage;
    String imageUniqueId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);

        sendMessageButton = findViewById(R.id.send_message_button_id);
        typingInput = findViewById(R.id.et_message_to_send);
        toolbar = findViewById(R.id.activit_chating_toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title_chating);
        messagesRecyclerview= findViewById(R.id.recyclerview_chating_id);
        imageSendMessage = findViewById(R.id.iv_sendImage);

        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setReverseLayout(true);
        messagesRecyclerview.setLayoutManager(linearLayoutManager);
        conversationAdapter = new ConversationAdapter(this,conversationList);

        contactUserId = getIntent().getStringExtra("userId");
        contactUsername = getIntent().getStringExtra("username");
        conversationId = getIntent().getStringExtra("conversationId");
        contactUrlProfile = getIntent().getStringExtra("contactUrlProfile");
        toolbarTitle.setText(contactUsername);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId  = pref.getString("token", null);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeMessage = "TEXT";
                if (!typingInput.getText().toString().isEmpty()) StartConversationWithMyContact();
            }
        });

        imageSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeMessage = "IMAGE";
                chooseImageAndSend();
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
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        final String createdAt = df.format(Calendar.getInstance().getTime());
        if (conversationId.equals("")){
            String uniqueID = UUID.randomUUID().toString();
            conversationId = uniqueID;
            saveConversation(contactUsername,"Mensaje provicinal :(", createdAt, contactUserId, uniqueID );
            getMessages();

        }else{
            sendMessage(conversationId);
        }
    }

    public void saveConversation(final String username, final String message, final String createdAt,  final String secondUserId, final String conversationId){
        Chat chat = new Chat(username,message,createdAt,"", secondUserId, conversationId);
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
                                            message,
                                            "5:49 PM",
                                            conversationId,
                                            myUserId,
                                            contactUserId,
                                            user.getFullname(),
                                            contactUsername,
                                            user.getUrlProfile(),
                                            contactUrlProfile);

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


    public void sendMessage(final String conversationId){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String createdAt = df.format(Calendar.getInstance().getTime());
        message = typingInput.getText().toString();

        if (typeMessage.equals("TEXT")){
            Conversation conversation = new Conversation(message, createdAt, "NO_image",typeMessage,conversationId);
            String uniqueID = UUID.randomUUID().toString();
            databaseReference.child("Conversations").child(conversationId).child("Messages").child(uniqueID).setValue(conversation);
            databaseReference.child("User").child(myUserId).child("Conversations").child(conversationId).child("previewLastMessage").setValue(message);
            databaseReference.child("User").child(contactUserId).child("Conversations").child(conversationId).child("previewLastMessage").setValue(message);
            typingInput.setText("");


        }else{
            Conversation conversation = new Conversation(message, createdAt, uriImage.toString(),typeMessage,conversationId);
            String uniqueID = UUID.randomUUID().toString();
            imageUniqueId = uniqueID;
            databaseReference.child("Conversations").child(conversationId).child("Messages").child(uniqueID).setValue(conversation);
            databaseReference.child("User").child(myUserId).child("Conversations").child(conversationId).child("previewLastMessage").setValue(message);
            databaseReference.child("User").child(contactUserId).child("Conversations").child(conversationId).child("previewLastMessage").setValue(message);
            typingInput.setText("");


        }

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
                orderMessagesList(conversationList);
                messagesRecyclerview.setAdapter(conversationAdapter);
                messagesRecyclerview.scrollToPosition(messagesRecyclerview.getAdapter().getItemCount()-1);
                modifyCreatedAtToFormat(conversationList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
                 StartConversationWithMyContact();
                 uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void uploadImage(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String imageId = UUID.randomUUID().toString();
        StorageReference reference = storageReference.child("messageImages/"+ imageId);
        UploadTask uploadTask = reference.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ChatingActivity.this, "Imagen subida con exito!", Toast.LENGTH_SHORT).show();

            }
        });

        final StorageReference ref = storageReference.child("messageImages/"+ imageId);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()) {

                    throw task.getException();
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
                        imageUniqueId ="";
                    }
                } else {
                    Toast.makeText(ChatingActivity.this, "Hubo un problema con la descarga.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void orderMessagesList(ArrayList<Conversation> arraylist) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Collections.sort(arraylist, new Comparator<Conversation>() {
                @Override
                public int compare(Conversation o1, Conversation o2) {
                    try {
                        return simpleDateFormat.parse(o2.getCreated_At()).compareTo(simpleDateFormat.parse(o1.getCreated_At()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
    }

    private void modifyCreatedAtToFormat(ArrayList<Conversation> messageList) {

        for (int i=0; i < messageList.size(); i++){
            String createdAt = messageList.get(i).getCreated_At();


            try {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
                Date d  = sd.parse(createdAt);
                sd = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");
                String  newFormat  = sd.format(d);
                messageList.get(i).setCreated_At(newFormat);

            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
    }


}
