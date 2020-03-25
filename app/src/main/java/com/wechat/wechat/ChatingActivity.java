package com.wechat.wechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.wechat.wechat.adapters.ConversationAdapter;
import com.wechat.wechat.models.Conversation;

import java.util.ArrayList;

public class ChatingActivity extends AppCompatActivity {

    RecyclerView chating_list_recyclerview;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);
        Toolbar toolbar = findViewById(R.id.activit_chating_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        chating_list_recyclerview = findViewById(R.id.recyclerview_chating_id);
        linearLayoutManager = new LinearLayoutManager(this);
        chating_list_recyclerview.setLayoutManager(linearLayoutManager);



        ArrayList<Conversation> conversationList = new  ArrayList<>();
        conversationList.add(new Conversation("Holaaa!","5:45PM","NO-IMAGE","2121212121"));
        conversationList.add(new Conversation("Como estas","5:47PM","NO-IMAGE","2121212121"));
        conversationList.add(new Conversation("Yo estoy bien","6:00PM","NO-IMAGE","2121212121"));
        conversationList.add(new Conversation("Me agrada, un saludo","7:PM","NO-IMAGE","2121212121"));

        ConversationAdapter conversationAdapter = new ConversationAdapter(ChatingActivity.this,conversationList);
        chating_list_recyclerview.setAdapter(conversationAdapter);




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
