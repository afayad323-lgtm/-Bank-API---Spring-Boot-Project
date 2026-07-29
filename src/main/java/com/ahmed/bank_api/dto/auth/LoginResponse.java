package com.ahmed.bank_api.dto.auth;

public class LoginResponse {
    private String token;

    public LoginResponse(String token){
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
