package de.fraunhofer.iem.mois.example.api.util;

public class UserManagement {

    public static String hashPassword(String password, String hashFunction) {

        return hashFunction + hashFunction;
    }

    public static boolean validateEmail(String emailAddress) {
        if (emailAddress.contains("@"))
            return true;
        else
            return false;
    }

    public static String maskCreditCard(String creditCard){

        return creditCard.replace("3","*");
    }

    public static String configureEmail(String emailAddress, String server) {

        return server;
    }
}
