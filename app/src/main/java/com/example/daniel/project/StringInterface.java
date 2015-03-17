package com.example.daniel.project;

import android.util.Log;

/**
 * Created by daniel on 22/02/15.
 */
public class StringInterface {
    private static String LOG_TAG=StringInterface.class.getSimpleName();
    public static String urlSpecifier="url";
    public static String mailToSpecifier="mailTo";
    public static String subjectSpecifier="subject";

    public static String descriptURLMethod(String url){
        String returnValue=urlSpecifier+':'+url;
        return returnValue;
    }
    public static String descriptMailMethod(String mailTo, String subject){
        String returnValue=mailToSpecifier+':'+mailTo+","+subjectSpecifier+':'+subject;
        return returnValue;
    }
    public static String readURLMethod(String description){
        String[] elements =description.split(":");
        if (elements.length>1) {
            return elements[1];
        }else {
            return description;
        }
    }
    public static MailMethodSettings readMailMethod(String descrition){
        String mailTo="", subject="";
        String[] pairs = descrition.split(",");
        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split(":");
            if(keyValue[0].compareTo(mailToSpecifier)==0) {
                mailTo=keyValue[1];
            }else if(keyValue[0].compareTo(subjectSpecifier)==0) {
                subject=keyValue[1];
            }
        }
        return new MailMethodSettings(mailTo,subject);
    }
}
