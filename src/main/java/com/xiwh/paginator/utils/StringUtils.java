package com.xiwh.paginator.utils;

import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isEmpty(String str){
        return str == null || str.isEmpty();
    }

    public static int safeToInt(String str,int defaultVal){
        if(isEmpty(str)){
            return defaultVal;
        }
        if(isNumeric(str)){
            int temp = Integer.parseInt(str);
            if(temp>=0){
                defaultVal = temp;
            }
        }
        return defaultVal;
    }

    public static boolean isNumeric(String string){
        if(isEmpty(string)){
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }
}
