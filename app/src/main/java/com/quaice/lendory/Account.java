package com.quaice.lendory;

public class Account {
    private String name, phonenumber, password;

    public Account(String name, String phonenumber, String password) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.password = password;
    }

    public Account() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
