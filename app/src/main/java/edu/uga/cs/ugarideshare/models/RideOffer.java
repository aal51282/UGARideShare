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

    /**
     * Get the ID of the ride offer
     * @return ID of the ride offer.
     */
    public String getId() {
        return id;
    } // getId

    /**
     * Set the ID of the ride offer
     * @param id ID of the ride offer.
     */
    public void setId(String id) {
        this.id = id;
    } // setId

    /**
     * Get the ID of the driver offering the ride
     * @return ID of the driver offering the ride.
     */
    public String getDriverId() {
        return driverId;
    } // getDriverId

    /**
     * Set the ID of the driver offering the ride
     * @param driverId ID of the driver offering the ride.
     */
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    } // setDriverId

    /**
     * Get the email of the driver offering the ride
     * @return Email of the driver offering the ride.
     */
    public String getDriverEmail() {
        return driverEmail;
    } // getDriverEmail

    /**
     * Set the email of the driver offering the ride
     * @param driverEmail Email of the driver offering the ride.
     */
    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    } // setDriverEmail

    /**
     * Get the date and time of the ride
     * @return Date and time of the ride.
     */
    public long getDateTime() {
        return dateTime;
    } // getDateTime

    /**
     * Set the date and time of the ride
     * @param dateTime Date and time of the ride.
     */
    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    } // setDateTime

    /**
     * Get the starting location of the ride
     * @return Starting location of the ride.
     */
    public String getStartPoint() {
        return startPoint;
    } // getStartPoint

    /**
     * Set the starting location of the ride
     * @param startPoint Starting location of the ride.
     */
    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    } // setStartPoint

    /**
     * Get the destination of the ride
     * @return Destination of the ride.
     */
    public String getDestination() {
        return destination;
    } // getDestination

    /**
     * Set the destination of the ride
     * @param destination Destination of the ride.
     */
    public void setDestination(String destination) {
        this.destination = destination;
    } // setDestination

    /**
     * Get the status of the ride
     * @return Status of the ride.
     */
    public String getStatus() {
        return status;
    } // getStatus

    /**
     * Set the status of the ride
     * @param status Status of the ride.
     */
    public void setStatus(String status) {
        this.status = status;
    } // setStatus

    /**
     * Get the ID of the rider accepting the ride
     * @return ID of the rider accepting the ride.
     */
    public String getRiderId() {
        return riderId;
    } // getRiderId

    /**
     * Set the ID of the rider accepting the ride
     * @param riderId ID of the rider accepting the ride.
     */
    public void setRiderId(String riderId) {
        this.riderId = riderId;
    } // setRiderId

    /**
     * Get the email of the rider accepting the ride
     * @return Email of the rider accepting the ride.
     */
    public String getRiderEmail() {
        return riderEmail;
    } // getRiderEmail

    /**
     * Set the email of the rider accepting the ride
     * @param riderEmail Email of the rider accepting the ride.
     */
    public void setRiderEmail(String riderEmail) {
        this.riderEmail = riderEmail;
    } // setRiderEmail

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
    } // acceptRide

    /**
     * Get formatted date and time string
     * @return Formatted date and time string
     */
    public String getFormattedDateTime() {
        return new Date(dateTime).toString();
    }

    /**
     * Get string representation of the ride offer
     * @return String representation of the ride offer
     */
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
    } // toString
} // RideOffer