package com.aagsdevelopment.busplus.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class StateSessionManager {

    private final Context context;
    SharedPreferences statecheck;
    SharedPreferences.Editor editor;

    public static final String SESSION_STATE_CHECK = "StateCheck";

    private static final String SWITCH_ON = "On";

    public  StateSessionManager(Context _context, String sessionName) {

        context = _context;
        statecheck = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = statecheck.edit();


    }
    public boolean isOn() {
        if (statecheck.getBoolean(SWITCH_ON, false)) {
            return true;
        } else {
            return false;
        }
    }
    public void saveState( boolean doesitOn) {

        editor.putBoolean(SESSION_STATE_CHECK, doesitOn);
        editor.commit();

    }
}
