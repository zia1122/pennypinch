package com.alsiddiquetech.pennypinch.models;

public class User {
    private String name;
    private String email;
    private String password;
    private String gender;
    private String securityQuestion1;
    private String securityAnswer1;

    public User(String name, String email, String password, String gender,
                String securityQuestion1, String securityAnswer1) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.securityQuestion1 = securityQuestion1;
        this.securityAnswer1 = securityAnswer1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSecurityQuestion1() {
        return securityQuestion1;
    }

    public void setSecurityQuestion1(String securityQuestion1) {
        this.securityQuestion1 = securityQuestion1;
    }

    public String getSecurityAnswer1() {
        return securityAnswer1;
    }

    public void setSecurityAnswer1(String securityAnswer1) {
        this.securityAnswer1 = securityAnswer1;
    }

}
