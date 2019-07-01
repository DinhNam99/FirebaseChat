package com.dell.firebasechat.model;

public class Message {
    private String sender, receiver, message, time, imageMess;
    boolean isSeen;

    public Message(String sender, String receiver, String mess, String time, boolean seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = mess;
        this.isSeen = seen;
        this.time = time;
    }
    public Message(){}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageMess() {
        return imageMess;
    }

    public void setImageMess(String imageMess) {
        this.imageMess = imageMess;
    }
}
