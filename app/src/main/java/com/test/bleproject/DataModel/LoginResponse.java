package com.test.bleproject.DataModel;

public class LoginResponse {

    private boolean success;
    private int code;
    private String name;
    private String number;
    private String id;


    public String getName() {
        return name;
    }


    public String getNumber() {
        return number;
    }

    public String created_by() {
        return id;
    }

    public int getStatusCode() {
        return code;
    }

    public boolean getStatus() {
        return success;
    }

}
