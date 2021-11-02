package com.example.scheduler.testproject;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.OracleCodec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/*
Source: https://www.javacodegeeks.com/2012/11/sql-injection-in-java-application.html
 */

public class DatabaseHelper {

    private String url;
    private String dbName;
    private String driver;
    private String userName;
    private String password;
    private Connection conn = null;

    public void DatabaseHelper() {


        url = "jdbc:mysql://192.168.2.128:3306/";
        dbName = "scheduler";
        driver = "com.mysql.jdbc.Driver";
        userName = "root";
        password = "password";
    }

    public ResultSet getUserInformation(String userId) {
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + dbName, userName, password);

            Statement st = conn.createStatement();
            String query = "SELECT * FROM  User where userId='" + userId + "'";
            conn.close();
            return st.executeQuery(query);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String escapeUserInput(String userID){

        return ESAPI.encoder().encodeForSQL( new OracleCodec(), userID );
    }
}
