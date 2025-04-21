package edu.uga.cs.ugarideshare.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import edu.uga.cs.ugarideshare.models.User;
import edu.uga.cs.ugarideshare.models.RideOffer;
import edu.uga.cs.ugarideshare.models.RideRequest;
import edu.uga.cs.ugarideshare.models.AcceptedRide;

/**
 * FirebaseUtil provides methods for interacting with Firebase Realtime Database.
 */
public class FirebaseUtil {
    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    // References to different database nodes
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
        // Check if email already exists
        usersRef.orderByChild("email").equalTo(user.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists
                    callback.onError("Email already registered");
                } else {
                    // Email doesn't exist, create new user
                    String userId = usersRef.push().getKey();
                    user.setId(userId);
                    usersRef.child(userId).setValue(user)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Login a user with email and password
     * @param email User email
     * @param password User password
     * @param callback Callback interface to handle success or failure
     */
    public static void loginUser(String email, String password, final FirebaseCallback<User> callback) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean userFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        user.setId(snapshot.getKey());
                        if (user.getPassword().equals(password)) {
                            callback.onSuccess(user);
                            userFound = true;
                            break;
                        }
                    }
                    if (!userFound) {
                        callback.onError("Invalid email/password combination");
                    }
                } else {
                    callback.onError("User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Get all available ride offers
     * @param callback Callback interface to handle success or failure
     */
    public static void getAvailableRideOffers(final FirebaseCallback<List<RideOffer>> callback) {
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
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Get all available ride requests
     * @param callback Callback interface to handle success or failure
     */
    public static void getAvailableRideRequests(final FirebaseCallback<List<RideRequest>> callback) {
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
        String offerId = rideOffersRef.push().getKey();
        offer.setId(offerId);
        rideOffersRef.child(offerId).setValue(offer)
                .addOnSuccessListener(aVoid -> callback.onSuccess(offer))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Post a new ride request
     * @param request Ride request object
     * @param callback Callback interface to handle success or failure
     */
    public static void postRideRequest(RideRequest request, final FirebaseCallback<RideRequest> callback) {
        String requestId = rideRequestsRef.push().getKey();
        request.setId(requestId);
        rideRequestsRef.child(requestId).setValue(request)
                .addOnSuccessListener(aVoid -> callback.onSuccess(request))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Update an existing ride offer
     * @param offer Updated ride offer object
     * @param callback Callback interface to handle success or failure
     */
    public static void updateRideOffer(RideOffer offer, final FirebaseCallback<RideOffer> callback) {
        rideOffersRef.child(offer.getId()).setValue(offer)
                .addOnSuccessListener(aVoid -> callback.onSuccess(offer))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Update an existing ride request
     * @param request Updated ride request object
     * @param callback Callback interface to handle success or failure
     */
    public static void updateRideRequest(RideRequest request, final FirebaseCallback<RideRequest> callback) {
        rideRequestsRef.child(request.getId()).setValue(request)
                .addOnSuccessListener(aVoid -> callback.onSuccess(request))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Delete a ride offer
     * @param offerId ID of the ride offer to delete
     * @param callback Callback interface to handle success or failure
     */
    public static void deleteRideOffer(String offerId, final FirebaseCallback<Boolean> callback) {
        rideOffersRef.child(offerId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Delete a ride request
     * @param requestId ID of the ride request to delete
     * @param callback Callback interface to handle success or failure
     */
    public static void deleteRideRequest(String requestId, final FirebaseCallback<Boolean> callback) {
        rideRequestsRef.child(requestId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Accept a ride offer (as a rider)
     * @param offer Ride offer to accept
     * @param riderId ID of the rider accepting the offer
     * @param riderEmail Email of the rider accepting the offer
     * @param callback Callback interface to handle success or failure
     */
    public static void acceptRideOffer(RideOffer offer, String riderId, String riderEmail, final FirebaseCallback<AcceptedRide> callback) {
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
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Accept a ride request (as a driver)
     * @param request Ride request to accept
     * @param driverId ID of the driver accepting the request
     * @param driverEmail Email of the driver accepting the request
     * @param callback Callback interface to handle success or failure
     */
    public static void acceptRideRequest(RideRequest request, String driverId, String driverEmail, final FirebaseCallback<AcceptedRide> callback) {
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
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Confirm a ride took place (as either driver or rider)
     * @param ride Accepted ride to confirm
     * @param isDriver Whether the confirmation is coming from the driver
     * @param callback Callback interface to handle success or failure
     */
    public static void confirmRide(AcceptedRide ride, boolean isDriver, final FirebaseCallback<Boolean> callback) {
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
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
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
                                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.onError(databaseError.getMessage());
                        }
                    });
                } else {
                    callback.onError("Rider does not have enough points");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
                callback.onError(databaseError.getMessage());
            }
        });
    }
}