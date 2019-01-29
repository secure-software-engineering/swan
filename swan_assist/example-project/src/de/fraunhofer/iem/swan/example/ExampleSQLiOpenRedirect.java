package de.fraunhofer.iem.swan.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;


public class ExampleSQLiOpenRedirect {

    private Connection conn;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String userId = request.getParameter("userId");
            userId = encode(userId);

            Statement st = (Statement) conn.createStatement();
            String query = "SELECT * FROM User WHERE userId=’" + userId + "’;";
            ResultSet res = (ResultSet) st.executeQuery(query);
            String url = "https://" + userId + ".company.com";
            response.sendRedirect(url);
        } catch (Exception e) {
        }

    }

    private String encode(String userId) {
        HttpServletResponseWrapper encoder = new HttpServletResponseWrapper(null);
        String result = encoder.encodeRedirectURL(userId);
        return result;
    }
}

































