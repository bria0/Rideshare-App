package com.example.ridex.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class Users extends RealmObject {
    @PrimaryKey
    @Required
    private ObjectId _id;

    @Required
    private RealmList<String> confirmedRide;

    @Required
    private String email;

    @Required
    private String firstName;

    @Required
    private String lastName;

    private Long numberOfRidesDriven;

    private Long numberOfRidesTaken;

    private Long overAllReview;

    private String photoUrl;

    private String takingRide;

    @Required
    private String uid;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public RealmList<String> getConfirmedRide() { return confirmedRide; }
    public void setConfirmedRide(RealmList<String> confirmedRide) { this.confirmedRide = confirmedRide; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Long getNumberOfRidesDriven() { return numberOfRidesDriven; }
    public void setNumberOfRidesDriven(Long numberOfRidesDriven) { this.numberOfRidesDriven = numberOfRidesDriven; }

    public Long getNumberOfRidesTaken() { return numberOfRidesTaken; }
    public void setNumberOfRidesTaken(Long numberOfRidesTaken) { this.numberOfRidesTaken = numberOfRidesTaken; }

    public Long getOverAllReview() { return overAllReview; }
    public void setOverAllReview(Long overAllReview) { this.overAllReview = overAllReview; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getTakingRide() { return takingRide; }
    public void setTakingRide(String takingRide) { this.takingRide = takingRide; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}
