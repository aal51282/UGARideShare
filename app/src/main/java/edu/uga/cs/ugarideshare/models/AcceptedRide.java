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
    } // AcceptedRide Constructor

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
    } // AcceptedRide Constructor

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
    } // AcceptedRide Constructor

    // Getters and setters

    /**
     * Get the ID of the ride
     * @return The ID of the ride
     */
    public String getId() {
        return id;
    } // getId

    /**
     * Set the ID of the ride
     * @param id The ID of the ride
     */
    public void setId(String id) {
        this.id = id;
    } // setId

    /**
     * Get the ID of the driver
     * @return The ID of the driver
     */
    public String getDriverId() {
        return driverId;
    } // getDriverId

    /**
     * Set the ID of the driver
     * @param driverId The ID of the driver
     */
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    } // setDriverId

    /**
     * Get the ID of the rider
     * @return The ID of the rider
     */
    public String getRiderId() {
        return riderId;
    } // getRiderId

    /**
     * Set the ID of the rider
     * @param riderId The ID of the rider
     */
    public void setRiderId(String riderId) {
        this.riderId = riderId;
    } // setRiderId

    /**
     * Get the email of the driver
     * @return The email of the driver.
     */
    public String getDriverEmail() {
        return driverEmail;
    } // getDriverEmail

    /**
     * Set the email of the driver
     * @param driverEmail The email of the driver.
     */
    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    } // setDriverEmail

    /**
     * Get the email of the rider
     * @return The email of the rider.
     */
    public String getRiderEmail() {
        return riderEmail;
    } // getRiderEmail

    /**
     * Set the email of the rider
     * @param riderEmail The email of the rider.
     */
    public void setRiderEmail(String riderEmail) {
        this.riderEmail = riderEmail;
    } // setRiderEmail

    /**
     * Get the date and time of the ride
     * @return The date and time of the ride.
     */
    public long getDateTime() {
        return dateTime;
    } // getDateTime

    /**
     * Set the date and time of the ride
     * @param dateTime The date and time of the ride.
     */
    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    } // setDateTime

    /**
     * Get the start point of the ride
     * @return The start point of the ride.
     */
    public String getStartPoint() {
        return startPoint;
    } // getStartPoint

    /**
     * Set the start point of the ride
     * @param startPoint The start point of the ride.
     */
    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    } // setStartPoint

    /**
     * Get the destination of the ride
     * @return The destination of the ride.
     */
    public String getDestination() {
        return destination;
    } // getDestination

    /**
     * Set the destination of the ride
     * @param destination The destination of the ride.
     */
    public void setDestination(String destination) {
        this.destination = destination;
    } // setDestination

    /**
     * Get the points cost of the ride
     * @return The points cost of the ride.
     */
    public int getPoints() {
        return points;
    } // getPoints

    /**
     * Set the points cost of the ride
     * @param points The points cost of the ride.
     */
    public void setPoints(int points) {
        this.points = points;
    } // setPoints

    /**
     * Get the driver's confirmation of the ride
     * @return The driver's confirmation of the ride.
     */
    public boolean isDriverConfirmed() {
        return driverConfirmed;
    } // isDriverConfirmed

    /**
     * Set the driver's confirmation of the ride
     * @param driverConfirmed The driver's confirmation of the ride.
     */
    public void setDriverConfirmed(boolean driverConfirmed) {
        this.driverConfirmed = driverConfirmed;
    } // setDriverConfirmed

    /**
     * Get the rider's confirmation of the ride
     * @return The rider's confirmation of the ride.
     */
    public boolean isRiderConfirmed() {
        return riderConfirmed;
    } // isRiderConfirmed

    /**
     * Set the rider's confirmation of the ride
     * @param riderConfirmed The rider's confirmation of the ride.
     */
    public void setRiderConfirmed(boolean riderConfirmed) {
        this.riderConfirmed = riderConfirmed;
    } // setRiderConfirmed

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

    /**
     * Convert object to string
     * @return String representation of the object
     */
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
    } // toString
} // AcceptedRide