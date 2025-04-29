package edu.uga.cs.ugarideshare.models;

import java.util.Date;

/**
 * RideRequest class represents a ride requested by a rider.
 */
public class RideRequest {
    private String id;
    private String riderId;
    private String riderEmail;
    private long dateTime;
    private String startPoint;
    private String destination;
    private String status; // "available" or "accepted"
    private String driverId;
    private String driverEmail;

    /**
     * Default constructor required for Firebase
     */
    public RideRequest() {
        // Required empty constructor for Firebase
    } // RideRequest Constructor

    /**
     * Constructor to create a new ride request
     * @param riderId ID of the user requesting the ride
     * @param riderEmail Email of the user requesting the ride
     * @param dateTime Date and time of the ride (timestamp)
     * @param startPoint Starting location of the ride
     * @param destination Destination of the ride
     */
    public RideRequest(String riderId, String riderEmail, long dateTime, String startPoint, String destination) {
        this.riderId = riderId;
        this.riderEmail = riderEmail;
        this.dateTime = dateTime;
        this.startPoint = startPoint;
        this.destination = destination;
        this.status = "available";
    } // RideRequest Constructor

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    /**
     * Accept a ride request
     * @param driverId ID of the driver accepting the request
     * @param driverEmail Email of the driver accepting the request
     * @return true if the request was successfully accepted, false otherwise
     */
    public boolean acceptRequest(String driverId, String driverEmail) {
        if ("available".equals(this.status)) {
            this.driverId = driverId;
            this.driverEmail = driverEmail;
            this.status = "accepted";
            return true;
        }
        return false;
    } // acceptRequest

    /**
     * Get formatted date and time string
     * @return Formatted date and time string
     */
    public String getFormattedDateTime() {
        return new Date(dateTime).toString();
    }

    /**
     * Get a string representation of the ride request
     * @return String representation of the ride request
     */
    @Override
    public String toString() {
        return "RideRequest{" +
                "id='" + id + '\'' +
                ", riderId='" + riderId + '\'' +
                ", dateTime=" + getFormattedDateTime() +
                ", startPoint='" + startPoint + '\'' +
                ", destination='" + destination + '\'' +
                ", status='" + status + '\'' +
                '}';
    } // toString
} // RideRequest