package com.wechat.wechat.activities.auth;


import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.R;
import com.wechat.wechat.activities.MainActivity;
import com.wechat.wechat.helpers.EncriptHelper;
import com.wechat.wechat.models.User;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1001;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 10002;

    private static final String TAG = "LoginActivity";
    private static final String LOGIN_METHOD_GOOGLE = "GOOGLE";
    private static final String LOGIN_METHOD_FACEBOOK = "FACEBOOK";


    TextInputEditText emailInput, passwordInput;
    SignInButton loginButton;
    TextView loginWith;
    Button loginButtonWithEmail;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout linearLayoutProgress;
    RelativeLayout relativeLayoutFormLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();

        startFirebaseConfigurations(); //here were googleSignConfig
        googleSignInConfigurations();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        loginWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });


        loginButtonWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLoginWithEmail();
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        String token  = pref.getString("token", null);


        if(token != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    /** binding views from xml */

    public void bindViews(){
        emailInput = findViewById(R.id.tiet_email);
        passwordInput = findViewById(R.id.pass);
        loginButton = findViewById(R.id.sign_in_button);
        linearLayoutProgress = findViewById(R.id.progres_id);
        relativeLayoutFormLogin = findViewById(R.id.container_login);
        loginWith = findViewById(R.id.loginwith);
        loginButtonWithEmail = findViewById(R.id.login_button);
    }


    private void startFirebaseConfigurations() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    /** Here start google Authentication */
    public void googleSignInConfigurations(){
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
                    if (account != null){
                        Toast.makeText(this, "Google Sign in Succeeded", Toast.LENGTH_SHORT).show();
                        handleSingInResult(account);
                    }
                } catch (ApiException e) {
                    Log.w(TAG, "Google sign in failed", e);
                    Toast.makeText(this, "Google Sign in Failed " + e, Toast.LENGTH_SHORT).show();
                }
        }

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null ){
                    String email = data.getStringExtra("email");
                    String password = data.getStringExtra("password");
                    emailInput.setText(email);
                    passwordInput.setText(password);
                    passwordInput.setFocusable(true);
                    doLoginWithEmail();
                }
            }
        }
    }

    private  void handleSingInResult(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null){
                                savingUser(LOGIN_METHOD_GOOGLE, user.getDisplayName(), user.getEmail(),user.getUid(),user.getPhotoUrl().toString(), user.getDisplayName(),"NO_NECESARY");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(LoginActivity.this, "Firebase Authentication Succeeded ", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Firebase Authentication failed:"  + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /** Saving asset_user => Login method => Google */

    public  void savingUser(String loginMethod, String username, String email, String userId, String urlProfile, String fullname, String password){
        if (loginMethod.equals("GOOGLE")){
            final User user = new User(username,email, userId, urlProfile, fullname, password,"Online");
            databaseReference.child("User").child(user.getUserId()).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("token", user.getUserId());
                            editor.apply();
                            Toast.makeText(LoginActivity.this, R.string.login_succes, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    /** Login with Email */


    public void doLoginWithEmail(){
        String email = emailInput.getText().toString();
        linearLayoutProgress.setVisibility(View.VISIBLE);
        relativeLayoutFormLogin.setVisibility(View.GONE);

        databaseReference.child("User")
                .orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("LOGIN",dataSnapshot.toString());
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null){
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    try {

                                        String pass = childDataSnapshot.child("password").getValue().toString();
                                        String userId = childDataSnapshot.child("userId").getValue().toString();
                                        String passDecripted  = EncriptHelper.decrypt(pass);
                                        String password = passwordInput.getText().toString();

                                        if (password.equals(passDecripted)){
                                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", 0);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("token", userId);
                                            editor.apply();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }else{
                                            Toast.makeText(LoginActivity.this, "La contraseña es incorrecta", Toast.LENGTH_SHORT).show();
                                            linearLayoutProgress.setVisibility(View.GONE);
                                            relativeLayoutFormLogin.setVisibility(View.VISIBLE);
                                        }

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                            }
                        }else{
                            linearLayoutProgress.setVisibility(View.GONE);
                            relativeLayoutFormLogin.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "EL usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("FAILURE", databaseError.toString());
                    }
                });

    }


}
