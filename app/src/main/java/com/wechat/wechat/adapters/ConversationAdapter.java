package com.wechat.wechat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wechat.wechat.R;
import com.wechat.wechat.models.Conversation;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Conversation> conversationsList;


    public ConversationAdapter(Context context, ArrayList<Conversation> conversationsList) {
        this.context = context;
        this.conversationsList = conversationsList;
    }


    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(context).inflate(R.layout.message_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder viewHolder, int i) {
        final Conversation conversation = conversationsList.get(i);
        viewHolder.message.setText(conversation.getMessage());
        viewHolder.createdAt.setText(conversation.getCreated_At());
        if (conversation.getMessageType().equals("IMAGE")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            viewHolder.linearLayoutMessageContentId.setLayoutParams(params);
            viewHolder.message.setVisibility(View.GONE);
            viewHolder.linearLayoutMessageContentId.setBackgroundColor(Color.parseColor("#f2f2f2"));
            viewHolder.createdAt.setTextColor(Color.parseColor("#646464"));
            Picasso.get().load(conversation.getUrlImage()).into(viewHolder.imageMessage);
            viewHolder.imageMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return conversationsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView message, createdAt;
        private ImageView imageMessage;
        private LinearLayout linearLayoutMessageContentId;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_message);
            createdAt = itemView.findViewById(R.id.tv_createdAt);
            imageMessage = itemView.findViewById(R.id.iv_imageMessage);
            linearLayoutMessageContentId = itemView.findViewById(R.id.linearLayoutMessageContentId);

        }
    }
}
