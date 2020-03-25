package com.wechat.wechat;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.wechat.wechat.adapters.ChatAdapter;
import com.wechat.wechat.models.Chat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton buttonContacts;

    RecyclerView chat_list_recyclerview;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonContacts = findViewById(R.id.button_contacts_id);

        chat_list_recyclerview = findViewById(R.id.chats_recyclerview_id);
        linearLayoutManager = new LinearLayoutManager(this);
        chat_list_recyclerview.setLayoutManager(linearLayoutManager);

        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);


        ArrayList<Chat> chatList = new  ArrayList<>();
        chatList.add(new Chat("Jose Ignacio","Que onda!","jose@gmail.com", R.drawable.profile));
        chatList.add(new Chat("Yuliana Gpe","Holi!","yuli@gmail.com", R.drawable.profile));
        chatList.add(new Chat("Ricardo","Hola","jose@gmail.com", R.drawable.profile));
        chatList.add(new Chat("Rosaura","Holaa!","jose@gmail.com",R.drawable.profile));

        ChatAdapter chatAdapter = new ChatAdapter(this,chatList);
        chat_list_recyclerview.setAdapter(chatAdapter);

        chatAdapter.setOnChatClickListener(new ChatAdapter.OnChatClickListener() {
            @Override
            public void onChatListener(Chat chat) {
                Toast.makeText(MainActivity.this, "Chat de : "+ chat.getUsername(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ChatingActivity.class);
                startActivity(intent);
            }
        });


        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenContacts();
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    public void OpenContacts(){
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }
}
