package com.test.bleproject.DataModel;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ScannedInfoResponse {
    private String message;
    private int status;
    private String brta_reg_num;
    private String model;
    private List<Driver> drivers;
    private int prev_meter_reading;
    private int last_intake;
    private int vehicle_id;
    private double avg_km_l;
    private List<Fuel> fuel;
    private String serial_number;
    private double last_intake_price;


    // Getters and Setters
    public String getMessage() {
        return message;
    }


    public int getStatus() {
        return status;
    }


    public String getBrta_reg_num() {
        return brta_reg_num;
    }

    public double getlast_intake_price() {
        return last_intake_price;
    }
    public String getSerialNumber() {
        return serial_number;
    }

    public String getModel() {
        return model;
    }


    public List<Driver> getDrivers() {
        return drivers;
    }


    public int getPrev_meter_reading() {
        return prev_meter_reading;
    }


    public int getLast_intake() {
        return last_intake;
    }

    public int getvehicle_id() {
        return vehicle_id;
    }


    public double getAvg_km_l() {
        return avg_km_l;
    }


    public List<Fuel> getFuel(){
        return fuel;
    }



    // Inner class for Driver
    public static class Driver implements Parcelable {
        private int id;
        private String driver_id;
        private String driver_name;
        private String mobile;
        private String dl_number;

        protected Driver(Parcel in) {
            id = in.readInt();
            driver_id = in.readString();
            driver_name = in.readString();
            mobile = in.readString();
            dl_number = in.readString();
        }

        public static final Creator<Driver> CREATOR = new Creator<Driver>() {
            @Override
            public Driver createFromParcel(Parcel in) {
                return new Driver(in);
            }

            @Override
            public Driver[] newArray(int size) {
                return new Driver[size];
            }
        };

        // Getters and Setters
        public int getId() {
            return id;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public String getDriver_name() {
            return driver_name;
        }

        public String getMobile() {
            return mobile;
        }

        public String getDl_number() {
            return dl_number;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(driver_id);
            dest.writeString(driver_name);
            dest.writeString(mobile);
            dest.writeString(dl_number);
        }
    }


    //Inner class for Fuel
    public static class Fuel implements Parcelable{
        private int id;
        private String fuel_type;
        private String unit;
        private double unit_price;

        protected Fuel(Parcel in) {
            id = in.readInt();
            fuel_type = in.readString();
            unit = in.readString();
            unit_price = in.readDouble();
        }

        public static final Creator<Fuel> CREATOR = new Creator<Fuel>() {
            @Override
            public Fuel createFromParcel(Parcel in) {
                return new Fuel(in);
            }

            @Override
            public Fuel[] newArray(int size) {
                return new Fuel[size];
            }
        };

        public int getId(){
            return  id;
        }

        public String getFuel_type(){
            return fuel_type;
        }
        public String getUnit(){
            return unit;
        }

        public double getUnit_price(){
            return unit_price;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(fuel_type);
            dest.writeString(unit);
            dest.writeDouble(unit_price);
        }
    }
}


