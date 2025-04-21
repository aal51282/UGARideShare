package edu.uga.cs.ugarideshare.models;

import java.util.Date;

/**
 * RideOffer class represents a ride offered by a driver.
 */
public class RideOffer {
    private String id;
    private String driverId;
    private String driverEmail;
    private long dateTime;
    private String startPoint;
    private String destination;
    private String status; // "available" or "accepted"
    private String riderId;
    private String riderEmail;

    /**
     * Default constructor required for Firebase
     */
    public RideOffer() {
        // Required empty constructor for Firebase
    }

    /**
     * Constructor to create a new ride offer
     * @param driverId ID of the user offering the ride
     * @param driverEmail Email of the user offering the ride
     * @param dateTime Date and time of the ride (timestamp)
     * @param startPoint Starting location of the ride
     * @param destination Destination of the ride
     */
    public RideOffer(String driverId, String driverEmail, long dateTime, String startPoint, String destination) {
        this.driverId = driverId;
        this.driverEmail = driverEmail;
        this.dateTime = dateTime;
        this.startPoint = startPoint;
        this.destination = destination;
        this.status = "available";
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getRiderEmail() {
        return riderEmail;
    }

    public void setRiderEmail(String riderEmail) {
        this.riderEmail = riderEmail;
    }

    /**
     * Accept a ride offer
     * @param riderId ID of the user accepting the ride
     * @param riderEmail Email of the user accepting the ride
     * @return true if the ride was successfully accepted, false otherwise
     */
    public boolean acceptRide(String riderId, String riderEmail) {
        if ("available".equals(this.status)) {
            this.riderId = riderId;
            this.riderEmail = riderEmail;
            this.status = "accepted";
            return true;
        }
        return false;
    }

    /**
     * Get formatted date and time string
     * @return Formatted date and time string
     */
    public String getFormattedDateTime() {
        return new Date(dateTime).toString();
    }

    @Override
    public String toString() {
        return "RideOffer{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", dateTime=" + getFormattedDateTime() +
                ", startPoint='" + startPoint + '\'' +
                ", destination='" + destination + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}