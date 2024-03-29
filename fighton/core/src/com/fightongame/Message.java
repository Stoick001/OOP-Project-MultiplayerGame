package com.fightongame;

import java.io.Serializable;

public class Message implements Serializable {
    // Message Constants
    static final String PLAY = "play";
    static final String QUEE = "quee";
    static final String LOGIN = "login"; // email#password
    static final String REGISTER = "register"; // email#username#password
    static final String BYE = "bye";
    static final String UPDATE = "update";
    static final String DID = "did";
    static final String RECONNECT = "reconnect";
    static final String END = "end";

    public String type;
    public String sender;
    public String contents;
    public String recipient;

    public Message(String str) {
        String[] data = str.split(",");
        this.type = data[0];
        this.sender = data[1];
        this.contents = data[2];
        this.recipient = data[3];
    }

    public Message(String type, String sender, String contents, String recipient) {
        this.type = type;
        this.sender = sender;
        this.contents = contents;
        this.recipient = recipient;
    }

    public String getMessageString() {
        return type + "," + sender + "," + contents + "," +recipient;
    }
}