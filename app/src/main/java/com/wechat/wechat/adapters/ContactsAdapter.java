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
import com.wechat.wechat.models.Contact;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    ArrayList<Contact> contactsList;
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
        viewHolder.imageprofile.setImageDrawable(ContextCompat.getDrawable(context, contact.getImageId()));
        viewHolder.fullname.setText(contact.getFullname());
        viewHolder.status.setText(contact.getStatus());
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageprofile;
        public TextView fullname, status;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageprofile = itemView.findViewById(R.id.img_profile);
            fullname = itemView.findViewById(R.id.tv_fullname);
            status = itemView.findViewById(R.id.tv_status);
        }
    }
}
