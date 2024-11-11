package com.test.bleproject.DataModel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DashboardResponseBody implements Serializable {

    @SerializedName("status")
    private int status;

    @SerializedName("weekly_fuel_cost")
    private int weeklyFuelCost;

    @SerializedName("monthly_fuel_cost")
    private int monthlyFuelCost;

    @SerializedName("total_fuel_cost")
    private int totalFuelCost;

    @SerializedName("previous_fuel_histories")
    private List<PreviousFuelHistory> previousFuelHistories;

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getWeeklyFuelCost() { return weeklyFuelCost; }
    public void setWeeklyFuelCost(int weeklyFuelCost) { this.weeklyFuelCost = weeklyFuelCost; }

    public int getMonthlyFuelCost() { return monthlyFuelCost; }
    public void setMonthlyFuelCost(int monthlyFuelCost) { this.monthlyFuelCost = monthlyFuelCost; }

    public int getTotalFuelCost() { return totalFuelCost; }
    public void setTotalFuelCost(int totalFuelCost) { this.totalFuelCost = totalFuelCost; }

    public List<PreviousFuelHistory> getPreviousFuelHistories() { return previousFuelHistories; }
    public void setPreviousFuelHistories(List<PreviousFuelHistory> previousFuelHistories) { this.previousFuelHistories = previousFuelHistories; }
}
