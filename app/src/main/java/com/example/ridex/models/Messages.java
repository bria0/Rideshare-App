package com.example.ridex.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class Messages extends RealmObject {
    @PrimaryKey
    @Required
    private ObjectId _id;

    private String chatRoomID;

    private String firstName;

    private String lastName;

    private String messageSent;

    @Required
    private String senderUID;

    private String time;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public String getChatRoomID() { return chatRoomID; }
    public void setChatRoomID(String chatRoomID) { this.chatRoomID = chatRoomID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMessageSent() { return messageSent; }
    public void setMessageSent(String messageSent) { this.messageSent = messageSent; }

    public String getSenderUID() { return senderUID; }
    public void setSenderUID(String senderUID) { this.senderUID = senderUID; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
