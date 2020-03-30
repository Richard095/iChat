package com.wechat.wechat;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView recyclerviewNotification;
    FloatingActionButton sendReqFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        https://developer.android.com/guide/topics/ui/dialogs?hl=es-419#kotlin   recyclerviewNotification = findViewById(R.id.recyclerview_notification_id);
        sendReqFriend = findViewById(R.id.button_notification_id);

        sendReqFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }




}
