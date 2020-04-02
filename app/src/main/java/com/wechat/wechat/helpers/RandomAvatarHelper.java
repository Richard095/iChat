package com.wechat.wechat.helpers;

import java.util.ArrayList;
import java.util.Random;

public class RandomAvatarHelper {
    public  static ArrayList<String> avatars =  new ArrayList<>();

    public  static String getRandomAvatarUrl(){
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/2351/2351917.svg");
        avatars.add("https://image.flaticon.com/icons/svg/921/921071.svg");
        avatars.add("https://image.flaticon.com/icons/svg/145/145867.svg");
        avatars.add("https://image.flaticon.com/icons/svg/145/145859.svg");
        avatars.add("https://image.flaticon.com/icons/svg/236/236831.svg");
        avatars.add("https://image.flaticon.com/icons/svg/428/428933.svg");
        avatars.add("https://image.flaticon.com/icons/svg/1226/1226097.svg");
        avatars.add("https://image.flaticon.com/icons/svg/701/701993.svg");
        avatars.add("https://image.flaticon.com/icons/svg/702/702003.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/374/374792.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/374/374554.svg");
        avatars.add("https://image.flaticon.com/icons/svg/701/701996.svg");
        avatars.add("https://image.flaticon.com/icons/svg/163/163834.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/374/374770.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/195/195231.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/374/374800.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/374/374630.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/1081/1081104.svg");
        avatars.add("https://www.flaticon.com/premium-icon/icons/svg/1081/1081053.svg");
        avatars.add("https://image.flaticon.com/icons/svg/702/702005.svg");
        avatars.add("https://www.clipartmax.com/png/middle/257-2572603_user-man-social-avatar-profile-icon-man-avatar-in-circle.png");
        Random rand = new Random();
        return avatars.get(rand.nextInt(avatars.size()));
    }










}
