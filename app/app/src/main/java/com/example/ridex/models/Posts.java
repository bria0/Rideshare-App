package com.example.ridex.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import org.bson.types.ObjectId;

public class Posts extends RealmObject {
    @PrimaryKey
    @Required
    private ObjectId _id;

    private Integer availableSeats;

    private String carColor;

    private String carModel;

    private String carYear;

    private Double price;

    @Required
    private RealmList<String> confirmedUsers;

    @Required
    private String date;

    @Required
    private String fromLocation;

    private String licensePlate;

    @Required
    private Integer numberOfSeats;

    private String postDescription;

    @Required
    private String postStatus;

    @Required
    private String postedAs;

    @Required
    private String posterUID;

    private String time;

    @Required
    private String toLocation;

    // Standard getters & setters
    public ObjectId getId() { return _id; }
    public void setId(ObjectId _id) { this._id = _id; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public String getCarColor() { return carColor; }
    public void setCarColor(String carColor) { this.carColor = carColor; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getCarYear() { return carYear; }
    public void setCarYear(String carYear) { this.carYear = carYear; }

    public RealmList<String> getConfirmedUsers() { return confirmedUsers; }
    public void setConfirmedUsers(RealmList<String> confirmedUsers) { this.confirmedUsers = confirmedUsers; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public String getPostDescription() { return postDescription; }
    public void setPostDescription(String postDescription) { this.postDescription = postDescription; }

    public String getPostStatus() { return postStatus; }
    public void setPostStatus(String postStatus) { this.postStatus = postStatus; }

    public String getPostedAs() { return postedAs; }
    public void setPostedAs(String postedAs) { this.postedAs = postedAs; }

    public String getPosterUID() { return posterUID; }
    public void setPosterUID(String posterUID) { this.posterUID = posterUID; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
