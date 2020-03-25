package com.wechat.wechat.helpers;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
