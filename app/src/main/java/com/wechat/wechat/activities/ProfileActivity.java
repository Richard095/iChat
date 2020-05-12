package com.wechat.wechat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;
import com.wechat.wechat.R;
import com.wechat.wechat.activities.auth.LoginActivity;
import com.wechat.wechat.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST =2;


    ImageView thumbProfileImage,imageChangePhoto, editState;
    TextView profileTextView,usernameProfileText, emailProfileText;

    Toolbar toolbar;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String myUserId;

    Bitmap bitmap;
    Uri uriImage;
    Button closeSesion;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        bindViews();

        toolbarConfigurations();


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId = pref.getString("token", null);



        imageChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageAndSend();
            }
        });

        editState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        closeSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("token");
                editor.apply();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
            }
        });


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
        imageChangePhoto = findViewById(R.id.iv_chage_photo);
        editState =  findViewById(R.id.iv_edit_profile_state);
        closeSesion = findViewById(R.id.close_sesion_button);

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

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
                            User user = childDataSnapshot.getValue(User.class);
                            if (user != null){
                                usernameProfileText.setText(user.getUsername());
                                emailProfileText.setText(user.getEmail());
                                profileTextView.setText(user.getDescription());
                                Picasso.get()
                                        .load(user.getUrlProfile())
                                        .resize(500,500)
                                        .centerCrop()
                                        .placeholder(R.drawable.asset_waiting)
                                        .into(thumbProfileImage);
                            }
                        }
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
        StorageReference reference = storageReference.child("avatars/"+ imageId);
        UploadTask uploadTask = reference.putBytes(data);

        Toast.makeText(this, "Cambiando tu photo de perfil...", Toast.LENGTH_SHORT).show();

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileActivity.this, "Foto de perfil actualizada!", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Error al subir la foto!", Toast.LENGTH_SHORT).show();
                    }
                });

        final StorageReference ref = storageReference.child("avatars/"+ imageId);

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
                        databaseReference.child("User").child(myUserId).child("urlProfile").setValue(downloadUri.toString());
                        gettingMyData();
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "Hubo un problema con la descarga.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




    /** Update state */

    public void openDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_state_profile, null);

        final EditText inputTextUpdateState = dialogView.findViewById(R.id.et_update_state);
        final Button updateStateButton = dialogView.findViewById(R.id.btn_update_state);
        dialogBuilder.setView(dialogView);

        updateStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputTextUpdateState.getText().toString().isEmpty() ){
                    databaseReference.child("User")
                            .child(myUserId)
                            .child("description").setValue(inputTextUpdateState.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this, "Tu estado ha sido actualizado.", Toast.LENGTH_SHORT).show();
                                    dialogBuilder.dismiss();
                                    gettingMyData();
                                }
                            });
                }else{
                    Toast.makeText(ProfileActivity.this, "Debes ingresar texto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.show();

    }

}

