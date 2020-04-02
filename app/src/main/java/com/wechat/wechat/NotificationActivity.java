package com.wechat.wechat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.adapters.InvitationAdapter;
import com.wechat.wechat.helpers.ValidationHelper;
import com.wechat.wechat.models.Invitation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;


public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerviewNotification;
    FloatingActionButton sendReqFriend;
    LinearLayoutManager linearLayoutManager;
    InvitationAdapter invitationAdapter;
    Toolbar toolbar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String myUserId, someoneId, myUsername;
    ArrayList<Invitation> invitationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        sendReqFriend = findViewById(R.id.button_notification_id);
        recyclerviewNotification = findViewById(R.id.recyclerview_notification_id);
        toolbar = findViewById(R.id.main_activity_notifications_toolbar);
        setSupportActionBar();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerviewNotification.setLayoutManager(linearLayoutManager);
        invitationAdapter = new InvitationAdapter(this, invitationList);

        sendReqFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        invitationAdapter.setOnInvitationClickListener(new InvitationAdapter.OnInvitationClickListener() {
            @Override
            public void onInvitationClickListener(Invitation invitation) {

            }

            @Override
            public void onDeleteInvitationClickListener(Invitation invitation) {
                deleteInvitation(invitation.getUserId());
            }

            @Override
            public void onAcceptInvitationClickListener(Invitation invitation) {
                acceptInvitation(invitation.getUsername(), invitation.getUserId());
            }
        });


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId = pref.getString("token", null);

        startFirebaseConfiguration();
        gettingMyData();
        getMyInvitations();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void setSupportActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void startFirebaseConfiguration() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    /** Open form dialog  */

    public void openDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.costum_dialog, null);

        final EditText emailInput = dialogView.findViewById(R.id.et_send_Email);
        final Button sendInvitation = dialogView.findViewById(R.id.notification_send_req_button_id);
        dialogBuilder.setView(dialogView);

        sendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationHelper.validateEmail(emailInput)){
                    searchFriendAndSendInvitation(emailInput.getText().toString(), emailInput, sendInvitation);
                }else {
                    Toast.makeText(NotificationActivity.this, "Verifica que el correo que esta enviando exista", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogBuilder.show();

    }


    /** Send invitation */
    public void sendInvitation(final EditText editText, final Button sendInvitation) {
        Invitation invitation = new Invitation(myUsername, myUserId);
        if (someoneId != null) {
            databaseReference.child("User").child(someoneId).child("Invitations").child(myUserId).setValue(invitation)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NotificationActivity.this, "Invitación enviada", Toast.LENGTH_SHORT).show();
                            editText.setText("");
                            sendInvitation.setEnabled(false);
                            someoneId = "";
                        }
                    });
        }

    }


    /** Search somebody for send  to invitation */

    public void searchFriendAndSendInvitation(String email, final EditText editText, final Button sendInvitation) {
        databaseReference.child("User")
                .orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            someoneId = childDataSnapshot.child("userId").getValue().toString();
                            sendInvitation(editText, sendInvitation);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    /** Get my userData */

    public void gettingMyData() {
        databaseReference.child("User")
                .orderByChild("userId").equalTo(myUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            myUsername = childDataSnapshot.child("username").getValue().toString();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    /** Getting my invitations */

    public void getMyInvitations() {
        databaseReference.child("User").child(myUserId).child("Invitations")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        invitationList.clear();
                        for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                            Invitation invitation = objDataSnapshot.getValue(Invitation.class);
                            invitationList.add(invitation);
                        }
                        recyclerviewNotification.setAdapter(invitationAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /** Accept invitation */

    public void acceptInvitation(final String username, final String newFriendId){
        Invitation invitation = new Invitation(username,newFriendId);

        databaseReference.child("User").child(myUserId).child("Contacts").child(newFriendId).setValue(invitation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Invitation invitation = new Invitation(myUsername,myUserId);
                        databaseReference.child("User").child(newFriendId).child("Contacts").child(myUserId).setValue(invitation)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(NotificationActivity.this, "Ahora "+username+" es tu nuevo amigo", Toast.LENGTH_LONG).show();
                                        deleteInvitation(newFriendId);
                                    }
                                });
                    }
                });
    }

    /** Delete invitation */

    public void deleteInvitation(String newFriendId){
        databaseReference.child("User").child(myUserId).child("Invitations").child(newFriendId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NotificationActivity.this, "Invitation deleted", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
