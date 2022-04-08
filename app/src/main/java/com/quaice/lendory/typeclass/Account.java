package com.quaice.lendory.typeclass;

import java.util.ArrayList;

public class Account {
    private String name, phonenumber, password;
    private ArrayList<String> liked;

    public Account(String name, String phonenumber, String password, ArrayList<String> liked) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.password = password;
        this.liked = liked;
        if (liked.size() == 0) {
            this.liked = new ArrayList<>();
            this.liked.add("thisHashCodeNeverWillBeUsed");
        }
    }

    public void addnewliked(String hashnumber){
        liked.add(hashnumber);
    }
    public void removenewliked(String hashnumber){
        liked.remove(hashnumber);
    }

    public boolean checkifconsist(String hashnumber){
        for(int i = 0; i < liked.size(); i++){
            if(liked.get(i).equals(hashnumber)){
                return true;
            }
        }
        return false;
    }

    public Account() {}


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

    public ArrayList<String> getLiked() {
        return liked;
    }

    public void setLiked(ArrayList<String> liked) {
        this.liked = liked;
    }
}
