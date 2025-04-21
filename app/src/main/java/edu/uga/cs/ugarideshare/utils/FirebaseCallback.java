package edu.uga.cs.ugarideshare.utils;

/**
 * Interface to handle Firebase operation callbacks
 * @param <T> Type of data returned in the callback
 */
public interface FirebaseCallback<T> {
    /**
     * Called when the operation is successful
     * @param result The result of the operation
     */
    void onSuccess(T result);

    /**
     * Called when the operation fails
     * @param error Error message
     */
    void onError(String error);
}