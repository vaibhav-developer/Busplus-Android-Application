package com.aagsdevelopment.busplus.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class DriversSessionManager {

    SharedPreferences driversSession;
    SharedPreferences.Editor editor;


    //    Session Names
    public static final String SESSION_DRIVERSESSION = "driverLoginSession";
    public static final String SESSION_DRIVERREMEMBERME = "rememberMe";

    Context context;


//    USERS SESSION VARIABLES

    private static final String IS_DRIVERLOGIN = "IsLoggedIn";
//    public static final String KEY_DRIVERFULLNAME = "fullName";
    public static final String KEY_DRIVERUSERNAME = "userName";
    public static final String KEY_DRIVERPASSWORD = "password";
//    public static final String KEY_DRIVEREMAIL = "email";
//    public static final String KEY_DRIVERPHONENO = "phoneNo";

    //    REMEMBER ME SESSION MANAGER
    private static final String IS_DRIVERREMEMBERME = "IsRemeberMe";
    public static final String KEYDRIVERSESSION_PASSWORD = "password";
    public static final String KEYDRIVERSESSION_USERNAME = "username";

    public  DriversSessionManager(Context _context, String sessionName) {

        context = _context;
        driversSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = driversSession.edit();

    }
    /*
    User

    Login

    Session
     */


    public void createDriverLoginSession( String userName, String password) {

        editor.putBoolean(IS_DRIVERLOGIN, true);

//        editor.putString(KEY_DRIVERFULLNAME, fullName);
        editor.putString(KEY_DRIVERUSERNAME, userName);
        editor.putString(KEY_DRIVERPASSWORD, password);
//        editor.putString(KEY_DRIVEREMAIL, email);
//        editor.putString(KEY_DRIVERPHONENO, phoneNo);

        editor.commit();

    }

    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_DRIVERUSERNAME, driversSession.getString(KEY_DRIVERUSERNAME, null));
        userData.put(KEY_DRIVERPASSWORD, driversSession.getString(KEY_DRIVERPASSWORD, null));

        return userData;
    }

    public boolean checkLogin() {
        if (driversSession.getBoolean(IS_DRIVERLOGIN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutDriverFromSession() {
        editor.clear();
        editor.commit();
    }


    /*
    Remember Me

    Session

     */

    public void createDriverRememberMeSession(String userName, String password) {

        editor.putBoolean(IS_DRIVERREMEMBERME, true);
        editor.putString(KEYDRIVERSESSION_USERNAME, userName);
        editor.putString(KEYDRIVERSESSION_PASSWORD, password);


        editor.commit();

    }
    public HashMap<String, String> getRememberMeDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();


        userData.put(KEYDRIVERSESSION_USERNAME, driversSession.getString(KEYDRIVERSESSION_USERNAME, null));
        userData.put(KEYDRIVERSESSION_PASSWORD, driversSession.getString(KEYDRIVERSESSION_PASSWORD, null));

        return userData;
    }
    public boolean checkRememberMeLogin() {
        if (driversSession.getBoolean(IS_DRIVERREMEMBERME, false)) {
            return true;
        } else {
            return false;
        }
    }


}
