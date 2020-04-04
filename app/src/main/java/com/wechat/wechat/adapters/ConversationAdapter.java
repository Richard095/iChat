package com.wechat.wechat.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    public void onBindViewHolder(@NonNull final ConversationAdapter.ViewHolder viewHolder, int i) {

        final Conversation conversation = conversationsList.get(i);

        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        String myUserId  = pref.getString("token", null);
        viewHolder.messageText.setText(conversation.getMessage());
        if (!conversation.getSenderId().equals("")){
            assert myUserId != null;
            if(myUserId.equals(conversation.getSenderId())){
                viewHolder.linearLayoutMessageContent.setBackgroundResource(R.drawable.message_text_by_me_bg);
            }
        }
        viewHolder.createdAt.setText(conversation.getCreated_At());

        if (conversation.getMessageType().equals("IMAGE")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            viewHolder.linearLayoutMessageContent.setLayoutParams(params);
            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.linearLayoutMessageContent.setBackgroundColor(Color.parseColor("#f2f2f2"));
            viewHolder.createdAt.setTextColor(Color.parseColor("#646464"));

            Picasso
                    .get()
                    .load(conversation.getUrlImage())
                    .placeholder(R.drawable.asset_waiting)
                    .into(viewHolder.imageMessage);
            viewHolder.imageMessage.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public int getItemCount() {
        return conversationsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView messageText, createdAt;
        private ImageView imageMessage;
        private LinearLayout linearLayoutMessageContent;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message);
            createdAt = itemView.findViewById(R.id.tv_createdAt);
            imageMessage = itemView.findViewById(R.id.iv_imageMessage);
            linearLayoutMessageContent = itemView.findViewById(R.id.linearLayoutMessageContentId);
        }
    }
}
