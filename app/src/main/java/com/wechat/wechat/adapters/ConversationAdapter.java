package com.wechat.wechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wechat.wechat.R;
import com.wechat.wechat.models.Conversation;

import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Context context;
    ArrayList<Conversation> conversationsList;


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

    }

    @Override
    public int getItemCount() {
        return conversationsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView message, createdAt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_message);
            createdAt = itemView.findViewById(R.id.tv_createdAt);
        }
    }
}
