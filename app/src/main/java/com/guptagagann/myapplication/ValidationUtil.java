package com.guptagagann.myapplication;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

public class ValidationUtil {
    private ValidationUtil(){}

    public static final Pattern SPECIAL_CHARACTERS_REGEX;

    static {
        SPECIAL_CHARACTERS_REGEX = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
    }

    public static boolean stringIsEmpty(String stringToCheck){
        if (isEmpty(stringToCheck) || stringToCheck.trim().isEmpty())
            return true;
        return false;
    }

    public static boolean hasSpecialCharacters(String stringToCheck){
        if (SPECIAL_CHARACTERS_REGEX.matcher(stringToCheck).find())
            return true;
        return false;
    }

    public static boolean isEmail(String email){
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean passwordLengthNotGreaterThanSixCharacters(String password){
        if (password.length() < 6)
            return true;
        return false;
    }
}
