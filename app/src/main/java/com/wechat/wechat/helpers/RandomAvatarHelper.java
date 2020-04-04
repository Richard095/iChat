package com.wechat.wechat.helpers;

import java.util.ArrayList;
import java.util.Random;

public class RandomAvatarHelper {
    public  static ArrayList<String> avatars =  new ArrayList<>();

    public  static String getRandomAvatarUrl(){
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F1081053.png?alt=media&token=c2794dfd-8251-4434-8ce9-066f27c37ce0");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F1081104.png?alt=media&token=a3b8d9a8-9d62-4fc2-81d6-4c22d1541ccc");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F195231.png?alt=media&token=499d4727-4efc-482d-9f1d-7c4a82fbb20e");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F2351917.png?alt=media&token=451c16e3-80bc-4f4c-bda6-c6472611b05e");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F374554.png?alt=media&token=75bbf329-c374-4fda-9a76-a3196c183c0d");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F374630.png?alt=media&token=5aaf7622-0316-4f09-a198-f0530f533dbc");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F374770.png?alt=media&token=93bf74d5-dbfa-4a52-85bd-c37aba3fe915");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F374792.png?alt=media&token=b72247ec-25bb-402d-aadc-11f88c015211");
        avatars.add("https://firebasestorage.googleapis.com/v0/b/ichat-3d0ce.appspot.com/o/avatars%2F374800.png?alt=media&token=c6af72f1-13e3-4604-93eb-a833491291e5");
        avatars.add("https://www.clipartmax.com/png/middle/257-2572603_user-man-social-avatar-profile-icon-man-avatar-in-circle.png");
        Random rand = new Random();
        return avatars.get(rand.nextInt(avatars.size()));
    }










}
