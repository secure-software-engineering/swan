package com.travelguru.data;

public class TravelOffer {

    double cost;
    double bookingFee;
    String title;
    String dateValid;
    int points;


    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getBookingFee() {
        return bookingFee;
    }

    public void setBookingFee(double bookingFee) {
        this.bookingFee = bookingFee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateValid() {
        return dateValid;
    }

    public void setDateValid(String dateValid) {
        this.dateValid = dateValid;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
