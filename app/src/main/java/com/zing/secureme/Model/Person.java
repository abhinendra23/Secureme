package com.zing.secureme.Model;

import java.util.HashMap;

public class Person implements Cloneable{
    String name,email,userId;
    HashMap<String,History> myHistory;
    public Person() {

    }
    public Object clone() throws
            CloneNotSupportedException
    {
        return super.clone();
    }

    public Person(String name, String email, String userId, HashMap<String, History> myHistory) {
        this.name = name;

        this.email = email;
        this.userId = userId;
        this.myHistory = myHistory;
    }

    public HashMap<String, History> getMyPost() {
        return myHistory;
    }

    public void setMyPost(HashMap<String, History> myPost) {
        this.myHistory = myPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
