package com.example.reactivepractice.api.model;

public class MemberLoginDetails {
    private String password;
    private String email;

    public MemberLoginDetails(String username, String password) {
        this.password = password;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
