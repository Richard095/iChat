package com.wechat.wechat.helpers;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {

    /** Validate email*/
    public static boolean validateEmail(EditText argEditText) {
        try {
            Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            Matcher matcher = pattern.matcher(argEditText.getText());
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
