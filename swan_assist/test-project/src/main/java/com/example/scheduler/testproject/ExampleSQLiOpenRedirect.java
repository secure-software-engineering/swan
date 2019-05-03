package com.example.scheduler.testproject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ExampleSQLiOpenRedirect {

    private Connection conn;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            //Get user ID from request
            String userId = Utils.encodeForSQL(request.getParameter("userId"));

            //Create SQL statement using user ID and then execute query
            Statement st = conn.createStatement();
            String query = "SELECT * FROM User WHERE userId='" + userId + "';";
            ResultSet res = st.executeQuery(query);

            //Create URL using user ID
            String url = "https://" + userId + ".company.com";

            //redirect to the URL
            response.sendRedirect(url);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

































