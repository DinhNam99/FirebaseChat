package com.dell.firebasechat.model;

public class User {
    private String id;
    private String username;
    private String image;
    private Friend contacts;
    public User(String id, String username,String image) {
        this.id = id;
        this.username = username;
        this.image = image;
    }
    public User(){
    }

    public Friend getContacts() {
        return contacts;
    }

    public void setContacts(Friend contacts) {
        this.contacts = contacts;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
