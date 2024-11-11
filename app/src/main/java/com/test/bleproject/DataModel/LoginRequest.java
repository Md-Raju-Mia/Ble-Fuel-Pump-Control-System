package com.test.bleproject.DataModel;

public class LoginRequest {
    private String username;
    private String password;


    public LoginRequest(String username, String password, String created_by){
        this.username = username;
        this.password = password;
    }
}
