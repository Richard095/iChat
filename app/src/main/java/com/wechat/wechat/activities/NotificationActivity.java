package com.wechat.wechat.activities;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wechat.wechat.R;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    ArrayList<Invitation> myContactList = new ArrayList<>();
    ImageView defaultImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        bindViews();

        setSupportActionBar();

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerviewNotification.setLayoutManager(linearLayoutManager);
        invitationAdapter = new InvitationAdapter(this, invitationList);

        onClickEvents();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        myUserId = pref.getString("token", null);

        startFirebaseConfiguration();
        gettingMyData();
        getMyInvitations();

        fetchMyContacts(); //For not have user duplicates

    }


    public void bindViews(){
        sendReqFriend = findViewById(R.id.button_notification_id);
        recyclerviewNotification = findViewById(R.id.recyclerview_notification_id);
        toolbar = findViewById(R.id.main_activity_notifications_toolbar);
        defaultImage = findViewById(R.id.iv_default_new_invitation);
    }

    /** Click events initialization*/

    public void onClickEvents(){

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
        View dialogView = inflater.inflate(R.layout.dialog_send_invitation, null);

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
        sendInvitation.setText("Enviando...");
        if (someoneId != null) {
            databaseReference.child("User").child(someoneId).child("Invitations").child(myUserId).setValue(invitation)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NotificationActivity.this, "Invitación enviada", Toast.LENGTH_SHORT).show();
                            sendInvitation.setEnabled(false);
                            sendInvitation.setText("Enviar invitación");
                            editText.setText("");
                            someoneId = "";
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NotificationActivity.this, "Conexión no establecida", Toast.LENGTH_SHORT).show();
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



                        if (dataSnapshot.getValue() == null){
                            Toast.makeText(NotificationActivity.this, "No hay ningun usuario asociado a este correo", Toast.LENGTH_LONG).show();
                        }else{

                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                someoneId = childDataSnapshot.child("userId").getValue().toString();
                                if (someoneId.equals(myUserId)){
                                    Toast.makeText(NotificationActivity.this, "No te puedes enviar invitacion a ti mismo", Toast.LENGTH_LONG).show();
                                }else{
                                    for (Invitation listContacts: myContactList){
                                        if (listContacts.getUserId().equals(someoneId)){
                                            Toast.makeText(NotificationActivity.this, getString(R.string.contact_existent_message), Toast.LENGTH_LONG).show();
                                            break;
                                        }
                                        if (!listContacts.getUserId().equals(someoneId)){
                                            sendInvitation(editText, sendInvitation);
                                        }
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

                        if (invitationList.size() == 0){
                            defaultImage.setVisibility(View.VISIBLE);
                        }else{
                            defaultImage.setVisibility(View.GONE);
                        }

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
                        //Toast.makeText(NotificationActivity.this, "Invitation deleted", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /** Checking if the my contacts*/
    public void fetchMyContacts(){
        databaseReference.child("User").child(myUserId).child("Contacts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myContactList.clear();

                        for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {
                            Invitation invitation = objDataSnapshot.getValue(Invitation.class);
                            myContactList.add(invitation);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
