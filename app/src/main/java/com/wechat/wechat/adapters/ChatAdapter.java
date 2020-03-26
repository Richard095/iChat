package com.wechat.wechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wechat.wechat.R;
import com.wechat.wechat.models.Chat;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    ArrayList<Chat> chatList;

    private OnChatClickListener onChatClickListener;

    public  interface OnChatClickListener{
        void onChatListener(Chat chat);
    }

    public void setOnChatClickListener(OnChatClickListener onChatClickListener){
        this.onChatClickListener = onChatClickListener;
    }

    public ChatAdapter(Context context, ArrayList<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(context).inflate(R.layout.chats_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
        final Chat chat = chatList.get(i);
        //viewHolder.imageprofile.setImageDrawable(ContextCompat.getDrawable(context, chat.getProfile()));
        viewHolder.fullname.setText(chat.getUsername());
        viewHolder.message_preview.setText(chat.getMessage());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChatClickListener.onChatListener(chat);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageprofile;
        public TextView fullname, message_preview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageprofile = itemView.findViewById(R.id.img_profile_chat);
            fullname = itemView.findViewById(R.id.tv_fullname_chat);
            message_preview = itemView.findViewById(R.id.tv_message_preview_chat);
        }
    }
}
