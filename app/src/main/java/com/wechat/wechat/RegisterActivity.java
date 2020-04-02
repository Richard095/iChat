package com.wechat.wechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wechat.wechat.helpers.EncriptHelper;
import com.wechat.wechat.helpers.ValidationHelper;
import com.wechat.wechat.models.User;

import java.util.UUID;


public class RegisterActivity extends AppCompatActivity {

    TextInputEditText fullnameInput;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextView cancel, save;
    ProgressBar progressBar;
    LinearLayout linearLayoutContainerForm;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cancel = findViewById(R.id.tv_cancel);
        save = findViewById(R.id.tv_save);
        fullnameInput = findViewById(R.id.tie_fullname);
        emailInput = findViewById(R.id.tie_email);
        passwordInput = findViewById(R.id.tie_password);
        progressBar = findViewById(R.id.pb_progressbar);
        linearLayoutContainerForm = findViewById(R.id.container_form);
        startFirebaseConfigurations();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUser();
            }
        });


    }

    private void startFirebaseConfigurations() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /**Create and Saving new user */

    public void  createNewUser(){

        if (!fullnameInput.getText().toString().isEmpty()
                || !passwordInput.getText().toString().isEmpty()
                || !emailInput.getText().toString().isEmpty()
        ){
            String password = passwordInput.getText().toString();
            try {

                if (ValidationHelper.validateEmail(emailInput)){
                    String fullname = fullnameInput.getText().toString();
                    String email = emailInput.getText().toString();

                    String passwordEncripted = EncriptHelper.encrypt(password);
                    String userId = UUID.randomUUID().toString();
                    String randomImageUrl =  "https://www.clipartmax.com/png/middle/257-2572603_user-man-social-avatar-profile-icon-man-avatar-in-circle.png";
                    User user = new User(fullname,email,userId,randomImageUrl,fullname,passwordEncripted);
                    saveUser(userId,user,password);
                }else{
                    Toast.makeText(this, "El correo no es valido", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception exe){ System.out.println(exe); }
        }else{ Toast.makeText(this, "Todo los campos son obligatorios.", Toast.LENGTH_SHORT).show(); }


    }

    public void saveUser(final String userId, final User user, final String pass){
        linearLayoutContainerForm.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("User").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this,"Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("password", pass);
                        resultIntent.putExtra("email", user.getEmail());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this,"Algo paso, intenta de nuevo o comprueba tu conexión a internet", Toast.LENGTH_SHORT).show();
                        linearLayoutContainerForm.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

}

