package com.aagsdevelopment.busplus.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class ThisDriverSessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

// SESSION NAMES
    public static final String SESSION_USERSSESSION = "userLoginSession";


//    SESSION VARIABLES

    private static final String DATA_PRESENT = "YES";
    public  String KEY_THIS_DRIVER_USERNAME = "DriverUsername";
    public  String KEY_THIS_DRIVER_BUSID = "busID";


    public ThisDriverSessionManager(Context _context, String sessionName) {

        context = _context;
        usersSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();


    }

    public void createThisDriverSessionManager(String username,String busid) {

        editor.putBoolean(DATA_PRESENT, true);

        editor.putString(KEY_THIS_DRIVER_USERNAME, username);
        editor.putString(KEY_THIS_DRIVER_BUSID, busid);


        editor.commit();

    }
    public HashMap<String, String> getThisDriverDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_THIS_DRIVER_USERNAME, usersSession.getString(KEY_THIS_DRIVER_USERNAME, null));
        userData.put(KEY_THIS_DRIVER_BUSID, usersSession.getString(KEY_THIS_DRIVER_BUSID, null));


        return userData;
    }

    public boolean isDriverDataPresent() {
        if (usersSession.getBoolean(DATA_PRESENT, false)) {
            return true;
        } else {
            return false;
        }
    }
}
