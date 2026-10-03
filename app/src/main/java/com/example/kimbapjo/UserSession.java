package com.example.kimbapjo;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREF_NAME = "kimbapjo_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences preferences;

    public UserSession(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLogin(int userId, String role) {
        preferences.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    public String getRole() {
        return preferences.getString(KEY_ROLE, "");
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
