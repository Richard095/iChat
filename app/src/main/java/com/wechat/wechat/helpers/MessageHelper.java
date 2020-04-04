package com.wechat.wechat.helpers;

import com.wechat.wechat.models.Conversation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MessageHelper {

    public static void orderMessagesList(ArrayList<Conversation> arraylist) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Collections.sort(arraylist, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                try {
                    return simpleDateFormat.parse(o2.getCreated_At()).compareTo(simpleDateFormat.parse(o1.getCreated_At()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }


    public static void modifyCreatedAtToFormat(ArrayList<Conversation> messageList) {

        for (int i=0; i < messageList.size(); i++){
            String createdAt = messageList.get(i).getCreated_At();


            try {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
                Date d  = sd.parse(createdAt);
                sd = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");
                String  newFormat  = sd.format(d);
                messageList.get(i).setCreated_At(newFormat);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

}
