package com.aagsdevelopment.busplus.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionSaveBusNumber {
    SharedPreferences BusNumberSession;
    SharedPreferences.Editor editor;


//    Session Name

    public static final String SESSION_SAVEBUSNUMBER = "savebusnumber";
    private static final String IS_BUSNUMBERPRESENT = "YES";
    public  String KEY_BUSNUMBER = "userName";
    public  String KEY_SEQUENCE = "sequence";
    public  String KEY_ODOMETER = "password";
    private final Context context;

    public SessionSaveBusNumber(Context _context, String sessionName) {

        context = _context;
        BusNumberSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = BusNumberSession.edit();

    }

    public void createSaveBusNumberSession( String busNumber, String odometer,String Sequence) {

        editor.putBoolean(IS_BUSNUMBERPRESENT, true);
        editor.putString(KEY_BUSNUMBER, busNumber);
        editor.putString(KEY_ODOMETER, odometer);
        editor.putString(KEY_SEQUENCE, Sequence);


        editor.commit();

    }
    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_BUSNUMBER, BusNumberSession.getString(KEY_BUSNUMBER, null));
        userData.put(KEY_SEQUENCE, BusNumberSession.getString(KEY_SEQUENCE, null));
        userData.put(KEY_ODOMETER, BusNumberSession.getString(KEY_ODOMETER, null));

        return userData;
    }
    public boolean checkBusNumberPresent() {
        if (BusNumberSession.getBoolean(IS_BUSNUMBERPRESENT, false)) {
            return true;
        } else {
            return false;
        }
    }
    public void clearBusNumber() {
        editor.clear();
        editor.commit();
    }

}
