package edu.uga.cs.ugarideshare.utils;

import android.content.Context;
import android.content.SharedPreferences;

import edu.uga.cs.ugarideshare.models.User;

/**
 * SessionManager handles user session management using SharedPreferences
 */
public class SessionManager {
    private static final String PREF_NAME = "UGARideShareSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    /**
     * Constructor
     * @param context Application context
     */
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * @param user User object
     */
    public void createLoginSession(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    /**
     * Get user ID
     * @return User ID
     */
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    /**
     * Get user email
     * @return User email
     */
    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clear session and log out user
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }
}