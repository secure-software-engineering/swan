package com.example.scheduler.testproject;

import javax.servlet.http.HttpServletResponseWrapper;

public class Utils {





    public static String encodeForSQL(String userId) {

        HttpServletResponseWrapper encoder = new HttpServletResponseWrapper(null);
        String result = encoder.encodeRedirectURL(userId);
        return result;
    }

    public static String encodeForOpenRedirect(String userId) {

        HttpServletResponseWrapper encoder = new HttpServletResponseWrapper(null);
        String result = encoder.encodeRedirectURL(userId);
        return result;
    }
}
 