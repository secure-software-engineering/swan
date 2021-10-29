package com.example.scheduler.testproject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class LogUtils {

    private static Logger logger = Logger.getLogger("MyLog");

    public static void logRecord(ResultSet resultSet){

        while (true) {
            try {
                if (!resultSet.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                logger.info( resultSet.getString("userId"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
