package com.aagsdevelopment.busplus.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class UserSessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;


    //    Session Names
    public static final String SESSION_USERSSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    Context context;


//    USERS SESSION VARIABLES

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_USERNAME = "userName";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONENO = "phoneNo";

    //    REMEMBER ME SESSION MANAGER
    private static final String IS_REMEMBERME = "IsRemeberMe";
    public static final String KEYSESSION_PASSWORD = "password";
    public static final String KEYSESSION_USERNAME = "phoneNo";

    public UserSessionManager(Context _context, String sessionName) {

        context = _context;
        usersSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();


    }
    /*
    User
    
    Login
    
    Session
     */


    public void createLoginSession(String fullName, String userName, String password, String email, String phoneNo) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONENO, phoneNo);

        editor.commit();

    }

    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_USERNAME, usersSession.getString(KEY_USERNAME, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PHONENO, usersSession.getString(KEY_PHONENO, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));

        return userData;
    }

    public boolean checkLogin() {
        if (usersSession.getBoolean(IS_LOGIN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }


    /*
    Remember Me

    Session

     */

    public void createRememberMeSession(String userName, String password) {

        editor.putBoolean(IS_REMEMBERME, true);
        editor.putString(KEYSESSION_USERNAME, userName);
        editor.putString(KEYSESSION_PASSWORD, password);


        editor.commit();

    }
    public HashMap<String, String> getRememberMeDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();


        userData.put(KEYSESSION_USERNAME, usersSession.getString(KEYSESSION_USERNAME, null));
        userData.put(KEYSESSION_PASSWORD, usersSession.getString(KEYSESSION_PASSWORD, null));

        return userData;
    }
    public boolean checkRememberMeLogin() {
        if (usersSession.getBoolean(IS_REMEMBERME, false)) {
            return true;
        } else {
            return false;
        }
    }


}
