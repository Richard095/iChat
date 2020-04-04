package com.wechat.wechat.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.wechat.wechat.R;
import com.wechat.wechat.helpers.EncriptHelper;
import com.wechat.wechat.helpers.RandomAvatarHelper;
import com.wechat.wechat.helpers.ValidationHelper;
import com.wechat.wechat.models.User;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {


    TextInputEditText fullnameInput;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextView startingRegister;

    TextView cancelTextView, saveTextView;
    ProgressBar progressBar;
    LinearLayout linearLayoutContainerForm;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindViews();

        startFirebaseConfigurations();

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUser();
            }
        });


    }

    public void bindViews(){
        cancelTextView = findViewById(R.id.tv_cancel);
        saveTextView = findViewById(R.id.tv_save);
        fullnameInput = findViewById(R.id.tie_fullname);
        emailInput = findViewById(R.id.tie_email);
        passwordInput = findViewById(R.id.tie_password);
        progressBar = findViewById(R.id.pb_progressbar);
        startingRegister = findViewById(R.id.tv_register_starting);
        linearLayoutContainerForm = findViewById(R.id.container_form);
    }

    private void startFirebaseConfigurations() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /**Create and Saving new asset_user */

    public void  createNewUser(){

        if (!fullnameInput.getText().toString().isEmpty()
                || !passwordInput.getText().toString().isEmpty()
                || !emailInput.getText().toString().isEmpty()
        ){

            try {
                String password = passwordInput.getText().toString();

                if (ValidationHelper.validateEmail(emailInput)){
                    String fullname = fullnameInput.getText().toString();
                    String email = emailInput.getText().toString();

                    String passwordEncripted = EncriptHelper.encrypt(password);
                    String userId = UUID.randomUUID().toString();
                    String randomImageUrl = RandomAvatarHelper.getRandomAvatarUrl();


                    User user = new User(fullname,email,userId,randomImageUrl,fullname,passwordEncripted);
                    saveUser(userId,user,password);
                }else{
                    Toast.makeText(this, "El correo no es valido", Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){ e.printStackTrace(); }
        }else{ Toast.makeText(this, "Todo los campos son obligatorios.", Toast.LENGTH_SHORT).show(); }

    }

    public void saveUser(final String userId, final User user, final String pass){
        linearLayoutContainerForm.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        startingRegister.setVisibility(View.VISIBLE);
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
                        startingRegister.setVisibility(View.VISIBLE);
                    }
                });
    }

}

