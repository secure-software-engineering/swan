package com.example.scheduler.testproject;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public User greeting(@RequestParam(value="userId", defaultValue="World") String userId) {

        //Use ID to get record from database (SQL injection
        DatabaseHelper databaseHelper = new DatabaseHelper();
        ResultSet resultSet = databaseHelper.getUserInformation(userId);

        //Write logs
        LogUtils.logRecord(resultSet);

        //Redirect request
        ExampleSQLiOpenRedirect redirect = new ExampleSQLiOpenRedirect();

        return new User(counter.incrementAndGet(),
                            String.format(template, userId), "word");
    }
}
