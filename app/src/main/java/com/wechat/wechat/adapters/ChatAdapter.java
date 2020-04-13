package com.wechat.wechat.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
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

        if (chat.getCountNewMessages() > 0){
            viewHolder.countNewMessages.setVisibility(View.VISIBLE);
            String count = Integer.toString(chat.getCountNewMessages());
            viewHolder.countNewMessages.setText(count);
        }else{
            viewHolder.countNewMessages.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(url)
                .resize(70,70)
                .centerCrop()
                .placeholder(R.drawable.asset_user)
                .into(viewHolder.imageProfile);

        viewHolder.fullname.setText(chat.getUsername());
        viewHolder.messagePreviewText.setText(chat.getMessage());
        viewHolder.createdAtPreview.setText(chat.getCreated_At());
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
        private ImageView imageProfile;
        private TextView fullname, messagePreviewText, createdAtPreview, countNewMessages;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.img_profile_chat);
            fullname = itemView.findViewById(R.id.tv_fullname_chat);
            messagePreviewText = itemView.findViewById(R.id.tv_message_preview_chat);
            createdAtPreview = itemView.findViewById(R.id.tv_message_preview_createdAt);
            countNewMessages = itemView.findViewById(R.id.tv_countMessage);
        }
    }
}
