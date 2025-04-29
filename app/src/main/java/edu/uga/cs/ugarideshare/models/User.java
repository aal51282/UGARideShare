package edu.uga.cs.ugarideshare.models;

/**
 * User class represents a user of the UGA RideShare app.
 * Each user has an email, password, and ride points.
 */
public class User {
    private String id;
    private String email;
    private String password; // Note: In a production app, we'd never store plaintext passwords
    private int ridePoints;

    /**
     * Default constructor required for Firebase
     */
    public User() {
        // Required empty constructor for Firebase
    }

    /**
     * Constructor to create a new user
     * @param email User's email address
     * @param password User's password
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.ridePoints = 100; // Default starting points
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRidePoints() {
        return ridePoints;
    }

    public void setRidePoints(int ridePoints) {
        this.ridePoints = ridePoints;
    }

    /**
     * Add points to the user's balance
     * @param points Number of points to add
     */
    public void addPoints(int points) {
        this.ridePoints += points;
    }

    /**
     * Subtract points from the user's balance
     * @param points Number of points to subtract
     * @return true if the user has enough points, false otherwise
     */
    public boolean subtractPoints(int points) {
        if (this.ridePoints >= points) {
            this.ridePoints -= points;
            return true;
        }
        return false;
    }

    /**
     * Convert the user object to a JSON string
     * @return JSON string representation of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", ridePoints=" + ridePoints +
                '}';
    } // toString
} // User