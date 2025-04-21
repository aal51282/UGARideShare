package edu.uga.cs.ugarideshare.models;

import java.util.Date;

/**
 * AcceptedRide class represents a ride that has been accepted by both driver and rider.
 */
public class AcceptedRide {
    private String id;
    private String driverId;
    private String riderId;
    private String driverEmail;
    private String riderEmail;
    private long dateTime;
    private String startPoint;
    private String destination;
    private int points;
    private boolean driverConfirmed;
    private boolean riderConfirmed;

    /**
     * Default constructor required for Firebase
     */
    public AcceptedRide() {
        // Required empty constructor for Firebase
    }

    /**
     * Constructor to create a new accepted ride from a ride offer
     * @param offer The ride offer that was accepted
     */
    public AcceptedRide(RideOffer offer) {
        this.driverId = offer.getDriverId();
        this.riderId = offer.getRiderId();
        this.driverEmail = offer.getDriverEmail();
        this.riderEmail = offer.getRiderEmail();
        this.dateTime = offer.getDateTime();
        this.startPoint = offer.getStartPoint();
        this.destination = offer.getDestination();
        this.points = 50; // Default points cost
        this.driverConfirmed = false;
        this.riderConfirmed = false;
    }

    /**
     * Constructor to create a new accepted ride from a ride request
     * @param request The ride request that was accepted
     */
    public AcceptedRide(RideRequest request) {
        this.driverId = request.getDriverId();
        this.riderId = request.getRiderId();
        this.driverEmail = request.getDriverEmail();
        this.riderEmail = request.getRiderEmail();
        this.dateTime = request.getDateTime();
        this.startPoint = request.getStartPoint();
        this.destination = request.getDestination();
        this.points = 50; // Default points cost
        this.driverConfirmed = false;
        this.riderConfirmed = false;
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

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isDriverConfirmed() {
        return driverConfirmed;
    }

    public void setDriverConfirmed(boolean driverConfirmed) {
        this.driverConfirmed = driverConfirmed;
    }

    public boolean isRiderConfirmed() {
        return riderConfirmed;
    }

    public void setRiderConfirmed(boolean riderConfirmed) {
        this.riderConfirmed = riderConfirmed;
    }

    /**
     * Check if the ride is confirmed by both parties
     * @return true if both driver and rider have confirmed the ride
     */
    public boolean isFullyConfirmed() {
        return driverConfirmed && riderConfirmed;
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
        return "AcceptedRide{" +
                "id='" + id + '\'' +
                ", driverId='" + driverId + '\'' +
                ", riderId='" + riderId + '\'' +
                ", dateTime=" + getFormattedDateTime() +
                ", startPoint='" + startPoint + '\'' +
                ", destination='" + destination + '\'' +
                ", points=" + points +
                ", driverConfirmed=" + driverConfirmed +
                ", riderConfirmed=" + riderConfirmed +
                '}';
    }
}