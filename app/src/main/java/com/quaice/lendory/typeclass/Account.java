package com.quaice.lendory.typeclass;

import java.util.ArrayList;

public class Account {
    private String name, phonenumber, password;
    private ArrayList<String> liked;
    private ArrayList<String> created;


    public Account(String name, String phonenumber, String password, ArrayList<String> liked, ArrayList<String> created) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.password = password;
        this.liked = liked;
        if (liked.size() == 0) {
            this.liked = new ArrayList<>();
            this.liked.add("thisHashCodeNeverWillBeUsed");
        }else {
            this.liked = liked;
        }
        if (created.size() == 0) {
            this.created = new ArrayList<>();
            this.created.add("thisHashCodeNeverWillBeUsedToo");
        }else {
            this.created = created;
        }
    }

    public void addnewliked(String hashnumber){
        liked.add(hashnumber);
    }
    public void removenewliked(String hashnumber){
        liked.remove(hashnumber);
    }

    public void addnewAdv(String hashnumber){
        created.add(hashnumber);
    }
    public void removeAdv(String hashnumber){
        created.remove(hashnumber);
    }

    public boolean checkifconsist(String hashnumber){
        try {
            for (int i = 0; i < liked.size(); i++) {
                try {
                    if (liked.get(i).equals(hashnumber)) {
                        return true;
                    }
                }catch (NullPointerException e){
                    continue;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public Account() {
        //created = new ArrayList<>();
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

    public ArrayList<String> getLiked() {
        return liked;
    }

    public void setLiked(ArrayList<String> liked) {
        this.liked = liked;
    }

    public ArrayList<String> getCreated() {
        return created;
    }

    public void setCreated(ArrayList<String> created) {
        this.created = created;
    }
}
