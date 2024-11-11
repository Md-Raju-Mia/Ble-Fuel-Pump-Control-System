package com.test.bleproject.DataModel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PreviousFuelHistory implements Serializable {
    @SerializedName("brta_reg_num")
    private String brtaRegNum;

    @SerializedName("vehicle_id")
    private int vehicleId;

    @SerializedName("driver_id")
    private int driverId;

    @SerializedName("driver_name")
    private String driverName;

    @SerializedName("fuel")
    private String fuel;

    @SerializedName("fuel_unit")
    private int fuelUnit;

    @SerializedName("fuel_cost")
    private int fuelCost;

    @SerializedName("date")
    private String date;

    // Getters and Setters
    public String getBrtaRegNum() { return brtaRegNum; }
    public void setBrtaRegNum(String brtaRegNum) { this.brtaRegNum = brtaRegNum; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getFuel() { return fuel; }
    public void setFuel(String fuel) { this.fuel = fuel; }

    public int getFuelUnit() { return fuelUnit; }
    public void setFuelUnit(int fuelUnit) { this.fuelUnit = fuelUnit; }

    public int getFuelCost() { return fuelCost; }
    public void setFuelCost(int fuelCost) { this.fuelCost = fuelCost; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
