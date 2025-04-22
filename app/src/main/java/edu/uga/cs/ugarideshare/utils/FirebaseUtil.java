package edu.uga.cs.ugarideshare.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.ugarideshare.models.AcceptedRide;
import edu.uga.cs.ugarideshare.models.RideOffer;
import edu.uga.cs.ugarideshare.models.RideRequest;
import edu.uga.cs.ugarideshare.models.User;

/**
 * FirebaseUtil provides methods for interacting with Firebase Realtime Database.
 */
public class FirebaseUtil {
    private static final String TAG = "FirebaseUtil";

    // Firebase Authentication instance
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Firebase Database references
    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private static final DatabaseReference usersRef = database.child("users");
    private static final DatabaseReference rideOffersRef = database.child("rideOffers");
    private static final DatabaseReference rideRequestsRef = database.child("rideRequests");
    private static final DatabaseReference acceptedRidesRef = database.child("acceptedRides");

    /**
     * Register a new user in Firebase
     * @param user User object with email and password
     * @param callback Callback interface to handle success or failure
     */
    public static void registerUser(User user, final FirebaseCallback<User> callback) {
        // First, create the user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnSuccessListener(authResult -> {
                    // Get the UID assigned by Firebase Auth
                    String userId = authResult.getUser().getUid();
                    user.setId(userId);

                    // Save user's password temporarily for login
                    String tempPassword = user.getPassword();

                    // For security, don't store the password in the database
                    user.setPassword(""); // Clear password before storing in database

                    // Create the user in the database
                    usersRef.child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                // Restore password for the callback
                                // (needed for session management but not stored in DB)
                                user.setPassword(tempPassword);
                                callback.onSuccess(user);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to save user to database", e);
                                callback.onError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create user with authentication", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Login a user with email and password
     * @param email User email
     * @param password User password
     * @param callback Callback interface to handle success or failure
     */
    public static void loginUser(String email, String password, final FirebaseCallback<User> callback) {
        // Authenticate with Firebase Auth
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Get user ID from authentication
                    String userId = authResult.getUser().getUid();

                    // Get user data from database
                    usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                user.setId(userId);

                                // Set the password for session management
                                // (Note: password isn't stored in DB but needed for local use)
                                user.setPassword(password);

                                callback.onSuccess(user);
                            } else {
                                // User exists in Auth but not in Database (rare case)
                                Log.w(TAG, "User authenticated but not found in database");
                                callback.onError("User profile not found");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Database error during login", databaseError.toException());
                            callback.onError(databaseError.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Authentication failed", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Get all available ride offers
     * @param callback Callback interface to handle success or failure
     */
    public static void getAvailableRideOffers(final FirebaseCallback<List<RideOffer>> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        rideOffersRef.orderByChild("status").equalTo("available").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RideOffer> offers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideOffer offer = snapshot.getValue(RideOffer.class);
                    offer.setId(snapshot.getKey());
                    offers.add(offer);
                }
                callback.onSuccess(offers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error fetching ride offers", databaseError.toException());
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Get all available ride requests
     * @param callback Callback interface to handle success or failure
     */
    public static void getAvailableRideRequests(final FirebaseCallback<List<RideRequest>> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        rideRequestsRef.orderByChild("status").equalTo("available").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RideRequest> requests = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RideRequest request = snapshot.getValue(RideRequest.class);
                    request.setId(snapshot.getKey());
                    requests.add(request);
                }
                callback.onSuccess(requests);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error fetching ride requests", databaseError.toException());
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Get all accepted rides for a specific user (as either driver or rider)
     * @param userId User ID
     * @param callback Callback interface to handle success or failure
     */
    public static void getAcceptedRidesForUser(String userId, final FirebaseCallback<List<AcceptedRide>> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        acceptedRidesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AcceptedRide> rides = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AcceptedRide ride = snapshot.getValue(AcceptedRide.class);
                    ride.setId(snapshot.getKey());
                    if (userId.equals(ride.getDriverId()) || userId.equals(ride.getRiderId())) {
                        rides.add(ride);
                    }
                }
                callback.onSuccess(rides);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error fetching accepted rides", databaseError.toException());
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Post a new ride offer
     * @param offer Ride offer object
     * @param callback Callback interface to handle success or failure
     */
    public static void postRideOffer(RideOffer offer, final FirebaseCallback<RideOffer> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        String offerId = rideOffersRef.push().getKey();
        offer.setId(offerId);
        rideOffersRef.child(offerId).setValue(offer)
                .addOnSuccessListener(aVoid -> callback.onSuccess(offer))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to post ride offer", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Post a new ride request
     * @param request Ride request object
     * @param callback Callback interface to handle success or failure
     */
    public static void postRideRequest(RideRequest request, final FirebaseCallback<RideRequest> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        String requestId = rideRequestsRef.push().getKey();
        request.setId(requestId);
        rideRequestsRef.child(requestId).setValue(request)
                .addOnSuccessListener(aVoid -> callback.onSuccess(request))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to post ride request", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Update an existing ride offer
     * @param offer Updated ride offer object
     * @param callback Callback interface to handle success or failure
     */
    public static void updateRideOffer(RideOffer offer, final FirebaseCallback<RideOffer> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        rideOffersRef.child(offer.getId()).setValue(offer)
                .addOnSuccessListener(aVoid -> callback.onSuccess(offer))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update ride offer", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Update an existing ride request
     * @param request Updated ride request object
     * @param callback Callback interface to handle success or failure
     */
    public static void updateRideRequest(RideRequest request, final FirebaseCallback<RideRequest> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        rideRequestsRef.child(request.getId()).setValue(request)
                .addOnSuccessListener(aVoid -> callback.onSuccess(request))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update ride request", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Delete a ride offer
     * @param offerId ID of the ride offer to delete
     * @param callback Callback interface to handle success or failure
     */
    public static void deleteRideOffer(String offerId, final FirebaseCallback<Boolean> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        rideOffersRef.child(offerId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete ride offer", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Delete a ride request
     * @param requestId ID of the ride request to delete
     * @param callback Callback interface to handle success or failure
     */
    public static void deleteRideRequest(String requestId, final FirebaseCallback<Boolean> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        rideRequestsRef.child(requestId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete ride request", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Accept a ride offer (as a rider)
     * @param offer Ride offer to accept
     * @param riderId ID of the rider accepting the offer
     * @param riderEmail Email of the rider accepting the offer
     * @param callback Callback interface to handle success or failure
     */
    public static void acceptRideOffer(RideOffer offer, String riderId, String riderEmail, final FirebaseCallback<AcceptedRide> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        // Update the offer status to accepted
        offer.acceptRide(riderId, riderEmail);
        rideOffersRef.child(offer.getId()).setValue(offer).addOnSuccessListener(aVoid -> {
            // Create a new accepted ride
            AcceptedRide acceptedRide = new AcceptedRide(offer);
            String rideId = acceptedRidesRef.push().getKey();
            acceptedRide.setId(rideId);

            // Save the accepted ride to Firebase
            acceptedRidesRef.child(rideId).setValue(acceptedRide)
                    .addOnSuccessListener(aVoid2 -> callback.onSuccess(acceptedRide))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save accepted ride", e);
                        callback.onError(e.getMessage());
                    });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to update ride offer status", e);
            callback.onError(e.getMessage());
        });
    }

    /**
     * Accept a ride request (as a driver)
     * @param request Ride request to accept
     * @param driverId ID of the driver accepting the request
     * @param driverEmail Email of the driver accepting the request
     * @param callback Callback interface to handle success or failure
     */
    public static void acceptRideRequest(RideRequest request, String driverId, String driverEmail, final FirebaseCallback<AcceptedRide> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        // Update the request status to accepted
        request.acceptRequest(driverId, driverEmail);
        rideRequestsRef.child(request.getId()).setValue(request).addOnSuccessListener(aVoid -> {
            // Create a new accepted ride
            AcceptedRide acceptedRide = new AcceptedRide(request);
            String rideId = acceptedRidesRef.push().getKey();
            acceptedRide.setId(rideId);

            // Save the accepted ride to Firebase
            acceptedRidesRef.child(rideId).setValue(acceptedRide)
                    .addOnSuccessListener(aVoid2 -> callback.onSuccess(acceptedRide))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to save accepted ride", e);
                        callback.onError(e.getMessage());
                    });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to update ride request status", e);
            callback.onError(e.getMessage());
        });
    }

    /**
     * Confirm a ride took place (as either driver or rider)
     * @param ride Accepted ride to confirm
     * @param isDriver Whether the confirmation is coming from the driver
     * @param callback Callback interface to handle success or failure
     */
    public static void confirmRide(AcceptedRide ride, boolean isDriver, final FirebaseCallback<Boolean> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        // Update the ride confirmation status
        if (isDriver) {
            ride.setDriverConfirmed(true);
        } else {
            ride.setRiderConfirmed(true);
        }

        // Update the ride in Firebase
        acceptedRidesRef.child(ride.getId()).setValue(ride).addOnSuccessListener(aVoid -> {
            // If both driver and rider have confirmed, update points
            if (ride.isFullyConfirmed()) {
                transferPoints(ride, callback);
            } else {
                callback.onSuccess(true);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to update ride confirmation status", e);
            callback.onError(e.getMessage());
        });
    }

    /**
     * Transfer points from rider to driver after ride is confirmed by both
     * @param ride Confirmed ride
     * @param callback Callback interface to handle success or failure
     */
    private static void transferPoints(AcceptedRide ride, final FirebaseCallback<Boolean> callback) {
        // Get rider and update points
        usersRef.child(ride.getRiderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot riderSnapshot) {
                User rider = riderSnapshot.getValue(User.class);
                rider.setId(riderSnapshot.getKey());

                // Deduct points from rider
                if (rider.subtractPoints(ride.getPoints())) {
                    // Update rider points in Firebase
                    usersRef.child(rider.getId()).child("ridePoints").setValue(rider.getRidePoints());

                    // Get driver and update points
                    usersRef.child(ride.getDriverId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot driverSnapshot) {
                            User driver = driverSnapshot.getValue(User.class);
                            driver.setId(driverSnapshot.getKey());

                            // Add points to driver
                            driver.addPoints(ride.getPoints());

                            // Update driver points in Firebase
                            usersRef.child(driver.getId()).child("ridePoints").setValue(driver.getRidePoints());

                            // Remove ride from accepted rides
                            acceptedRidesRef.child(ride.getId()).removeValue()
                                    .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to remove completed ride", e);
                                        callback.onError(e.getMessage());
                                    });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Database error getting driver data", databaseError.toException());
                            callback.onError(databaseError.getMessage());
                        }
                    });
                } else {
                    callback.onError("Rider does not have enough points");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error getting rider data", databaseError.toException());
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Get a user by ID
     * @param userId User ID
     * @param callback Callback interface to handle success or failure
     */
    public static void getUserById(String userId, final FirebaseCallback<User> callback) {
        // Check if user is authenticated
        if (firebaseAuth.getCurrentUser() == null) {
            callback.onError("User not authenticated");
            return;
        }

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setId(dataSnapshot.getKey());
                    callback.onSuccess(user);
                } else {
                    callback.onError("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error getting user by ID", databaseError.toException());
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Get the current authenticated user's ID
     * @return User ID or null if not authenticated
     */
    public static String getCurrentUserId() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    /**
     * Sign out the current user
     */
    public static void signOut() {
        firebaseAuth.signOut();
    }
}