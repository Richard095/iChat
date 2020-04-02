package com.wechat.wechat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wechat.wechat.R;
import com.wechat.wechat.models.Invitation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Invitation> invitationList;

    private OnInvitationClickListener onInvitationClickListener;

    public  interface OnInvitationClickListener{
        void onInvitationClickListener(Invitation invitation);
        void onDeleteInvitationClickListener(Invitation invitation);
        void onAcceptInvitationClickListener(Invitation invitation);
    }

    public void setOnInvitationClickListener(OnInvitationClickListener onInvitationClickListener){
        this.onInvitationClickListener = onInvitationClickListener;
    }

    public InvitationAdapter(Context context, ArrayList<Invitation> invitationListList) {
        this.context = context;
        this.invitationList = invitationListList;
    }

    @NonNull
    @Override
    public InvitationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(context).inflate(R.layout.invitation_item, parent, false);
        return  new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull InvitationAdapter.ViewHolder viewHolder, int position) {
        final Invitation invitation = invitationList.get(position);
        viewHolder.username.setText(invitation.getUsername());

        viewHolder.acceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInvitationClickListener.onAcceptInvitationClickListener(invitation);
            }
        });

        viewHolder.deleteInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInvitationClickListener.onDeleteInvitationClickListener(invitation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invitationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username ;
        private Button acceptInvitation;
        private ImageView deleteInvitation;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username_invitation);
            acceptInvitation = itemView.findViewById(R.id.button_accept_invitation);
            deleteInvitation = itemView.findViewById(R.id.iv_delete_invitation);
        }
    }
}
