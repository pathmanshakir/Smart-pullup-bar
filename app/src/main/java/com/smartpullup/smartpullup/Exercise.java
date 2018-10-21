package com.smartpullup.smartpullup;

import java.util.Date;

/**
 * Created by Jorren on 16/04/2018.
 */

public class Exercise {

    private Date date;
    private double maxSpeed;
    private double avgSpeed;
    private double totalTime;
    private int totalPullups;

    public Exercise() {
    }

    public Exercise(double maxSpeed, double avgSpeed, double totalTime, int totalPullups) {
        date = new Date(System.currentTimeMillis());//DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(System.currentTimeMillis());
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.totalTime = totalTime;
        this.totalPullups = totalPullups;
    }

    public Exercise(Date date, double maxSpeed, double avgSpeed, double totalTime, int totalPullups) {
        this.date = date;//new Date(System.currentTimeMillis());//DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(System.currentTimeMillis());
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.totalTime = totalTime;
        this.totalPullups = totalPullups;
    }

    public Date getDate() {
        return date;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public int getTotalPullups() {
        return totalPullups;
    }
}
