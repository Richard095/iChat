package com.wechat.wechat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wechat.wechat.models.User;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 1001;
    private static final String TAG = "LoginActivity";
    private static final String LOGIN_METHOD_GOOGLE = "GOOGLE";


    EditText username, password;
    SignInButton loginButton;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        loginButton = findViewById(R.id.sign_in_button);
        googleSignConfig();
        startFirebaseConf();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String token  = pref.getString("token", null); // getting String

        Toast.makeText(this, "Your token: " + token, Toast.LENGTH_SHORT).show();

        System.out.println("====================>Your token: "+token );

        if(token != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void startFirebaseConf() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //Here start google Autentication
    public void googleSignConfig(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println(connectionResult);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Toast.makeText(this, "Google Sign in Succeeded", Toast.LENGTH_SHORT).show();
                    handleSingInResult(account);
                } catch (ApiException e) {
                    Log.w(TAG, "Google sign in failed", e);
                    Toast.makeText(this, "Google Sign in Failed " + e, Toast.LENGTH_SHORT).show();
                }
            }

    }

    private  void handleSingInResult(GoogleSignInAccount account){
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "signInWithCredential:success: currentUser: "
                                    + user.getEmail()
                                    +" "+ user.getDisplayName()
                                    +" " +user.getPhoneNumber()
                                    +" " +user.getPhotoUrl()
                                    +" " +user.getUid()
                            );

                            saveUser(LOGIN_METHOD_GOOGLE, user.getDisplayName(), user.getEmail(),user.getUid(),user.getPhotoUrl().toString(), user.getDisplayName(),"NO_NECESARY");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginActivity.this, "Firebase Authentication Succeeded ", Toast.LENGTH_LONG).show();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Firebase Authentication failed:"  + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public  void saveUser(String loginMethod, String username, String email, String userId, String urlProfile, String fullname, String password){

        if (loginMethod.equals("GOOGLE")){
            User user = new User(username,email, userId, urlProfile, fullname, password);
            databaseReference.child("User").child(user.getUserId()).setValue(user);
            Toast.makeText(this, "Usuario registrado con exito!!", Toast.LENGTH_SHORT).show();


            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("token", user.getUserId()); // Storing string
            editor.commit();
        }
    }



}
