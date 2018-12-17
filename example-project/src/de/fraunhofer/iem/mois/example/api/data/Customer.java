package de.fraunhofer.iem.mois.example.api.data;

import de.fraunhofer.iem.mois.example.api.util.UserManagement;

public class Customer {

    private String name;
    String creditCard;
    String password;
    String email;
    double budget;


    public Customer(String customerName, String creditCard, double budget) {

        setName(customerName);
        setCreditCard(creditCard);
        setBudget(budget);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = UserManagement.hashPassword(password, "SHA-256");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (UserManagement.validateEmail(email))
            this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }


}
