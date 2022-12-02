package com.example.ridex.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class Ride extends RealmObject {
    @PrimaryKey
    @Required
    private ObjectId _id;

    @Required
    private String driverUID;

    private String fromLocation;

    private String rideDuration;

    private Posts rideID;

    private String rideStatus;

    @Required
    private RealmList<String> riderUIDs;

    private String toLocation;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public String getDriverUID() { return driverUID; }
    public void setDriverUID(String driverUID) { this.driverUID = driverUID; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getRideDuration() { return rideDuration; }
    public void setRideDuration(String rideDuration) { this.rideDuration = rideDuration; }

    public Posts getRideID() { return rideID; }
    public void setRideID(Posts rideID) { this.rideID = rideID; }

    public String getRideStatus() { return rideStatus; }
    public void setRideStatus(String rideStatus) { this.rideStatus = rideStatus; }

    public RealmList<String> getRiderUIDs() { return riderUIDs; }
    public void setRiderUIDs(RealmList<String> riderUIDs) { this.riderUIDs = riderUIDs; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }
}
