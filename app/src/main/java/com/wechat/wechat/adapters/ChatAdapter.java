package com.wechat.wechat.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wechat.wechat.R;
import com.wechat.wechat.models.Chat;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Chat> chatList;

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

        String url = chat.getUrlProfile();
        if (chat.getUrlProfile().equals("") ){
            url = "https://pngimage.net/wp-content/uploads/2018/05/button-profile-png-8.png";
        }


        Picasso.get()
                .load(url)
                .resize(70,70)
                .centerCrop()
                .into(viewHolder.imageprofile);

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
        private ImageView imageprofile;
        private TextView fullname, message_preview;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageprofile = itemView.findViewById(R.id.img_profile_chat);
            fullname = itemView.findViewById(R.id.tv_fullname_chat);
            message_preview = itemView.findViewById(R.id.tv_message_preview_chat);
        }
    }
}
