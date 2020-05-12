package com.wechat.wechat.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wechat.wechat.R;
import com.wechat.wechat.models.Conversation;

import java.io.File;
import java.util.ArrayList;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Conversation> conversationsList;

    private OnConversationClickListener onConversationClickListener;

    public  interface OnConversationClickListener{
        void onOpenImageClickListener(Conversation conversation);
    }
    public void setOnConversationClickListener(OnConversationClickListener onConversationClickListener){
        this.onConversationClickListener = onConversationClickListener;
    }
    public ConversationAdapter(Context context, ArrayList<Conversation> conversationsList) {
        this.context = context;
        this.conversationsList = conversationsList;
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(context).inflate(R.layout.message_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationAdapter.ViewHolder viewHolder, int i) {

        final Conversation conversation = conversationsList.get(i);
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        String myUserId  = pref.getString("token", null);
        viewHolder.messageText.setText(conversation.getMessage());
        viewHolder.createdAt.setText(conversation.getCreated_At());

        if (!conversation.getSenderId().equals("")){
            assert myUserId != null;
            if(myUserId.equals(conversation.getSenderId())){
                viewHolder.linearLayoutMessageContent.setBackgroundResource(R.drawable.message_text_by_me_bg);
            }else if(!myUserId.equals(conversation.getSenderId())) {
                viewHolder.linearLayoutMainContainer.setGravity(Gravity.START);
            }
        }

        if (conversation.getSenderId().equals(myUserId)){
            if (conversation.isRead()){
                viewHolder.iv_read.setVisibility(View.VISIBLE);
            }else{
                viewHolder.iv_read.setVisibility(View.GONE);
            }
        }

        if (conversation.getMessageType().equals("IMAGE")){
            String urlImage;
            if (conversation.getSenderId().equals(myUserId)){ //Si es mi mensaje
                urlImage = conversation.getSenderImageUrl();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                viewHolder.linearLayoutMessageContent.setLayoutParams(params);
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.linearLayoutMessageContent.setBackgroundColor(Color.parseColor("#f2f2f2"));
                viewHolder.createdAt.setTextColor(Color.parseColor("#646464"));
                viewHolder.imageMessage.setImageURI(Uri.fromFile(new File(urlImage)));
                viewHolder.imageMessage.setVisibility(View.VISIBLE);
                //viewHolder.imageButton.setVisibility(View.VISIBLE);
            }else if(!conversation.getSenderId().equals(myUserId)){

                //Si el mensaje es de mi contacto
                urlImage = conversation.getUrlImage();

                if (!urlImage.equals("NO_READY")){

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        viewHolder.linearLayoutMessageContent.setLayoutParams(params);
                        viewHolder.messageText.setVisibility(View.GONE);
                        viewHolder.linearLayoutMessageContent.setBackgroundColor(Color.parseColor("#f50057"));
                        viewHolder.createdAt.setTextColor(Color.parseColor("#ffffff"));
                        viewHolder.imageButton.setVisibility(View.VISIBLE);

                }else{
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    viewHolder.linearLayoutMessageContent.setLayoutParams(params);
                    viewHolder.messageText.setVisibility(View.GONE);
                    viewHolder.linearLayoutMessageContent.setBackgroundColor(Color.parseColor("#f2f2f2"));
                    viewHolder.createdAt.setTextColor(Color.parseColor("#646464"));
                    viewHolder.imageMessage.setImageDrawable(context.getResources().getDrawable(R.drawable.asset_waiting));
                    viewHolder.imageMessage.setVisibility(View.VISIBLE);
                }

            }
        }

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConversationClickListener.onOpenImageClickListener(conversation);
            }
        });


    }

    @Override
    public int getItemCount() {
        return conversationsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView messageText, createdAt;
        private ImageView imageMessage, iv_read;
        private LinearLayout linearLayoutMessageContent, linearLayoutMainContainer;
        private Button imageButton;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_message);
            createdAt = itemView.findViewById(R.id.tv_createdAt);
            imageMessage = itemView.findViewById(R.id.iv_imageMessage);
            linearLayoutMessageContent = itemView.findViewById(R.id.linearLayoutMessageContentId);
            linearLayoutMainContainer = itemView.findViewById(R.id.ll_main_message_container);
            iv_read = itemView.findViewById(R.id.iv_read);
            imageButton = itemView.findViewById(R.id.image_message_button);
        }
    }
}
