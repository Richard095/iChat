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
import com.wechat.wechat.models.Contact;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    ArrayList<Contact> contactsList;


    private OnContactClickListener onContactClickListener;

    public  interface OnContactClickListener{
        void onContactListener(Contact contact);
    }

    public void setOnContactClickListener(OnContactClickListener onContactClickListener){
        this.onContactClickListener = onContactClickListener;
    }


    public ContactsAdapter(Context context, ArrayList<Contact> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(context).inflate(R.layout.contact_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder viewHolder, int i) {
        final Contact contact = contactsList.get(i);
        Picasso.get()
                .load(contact.getProfileUrl())
                .resize(70,70)
                .centerCrop()
                .placeholder(R.drawable.asset_user)
                .into(viewHolder.imageProfile);

        viewHolder.fullname.setText(contact.getFullname());
        viewHolder.status.setText(contact.getStatus());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onContactClickListener.onContactListener(contact);
            }
        });


    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageProfile;
        private TextView fullname, status;
        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.img_profile);
            fullname = itemView.findViewById(R.id.tv_fullname);
            status = itemView.findViewById(R.id.tv_status);
        }
    }
}
