package com.aagsdevelopment.busplus.User;

public class DriverUsernameHelperClass {

    String Driverusername;
    String DriverUserID;

    public DriverUsernameHelperClass(String driverusername, String driverUserID) {
        Driverusername = driverusername;
        DriverUserID = driverUserID;
    }

    public String getDriverusername() {
        return Driverusername;
    }

    public void setDriverusername(String driverusername) {
        Driverusername = driverusername;
    }

    public String getDriverUserID() {
        return DriverUserID;
    }

    public void setDriverUserID(String driverUserID) {
        DriverUserID = driverUserID;
    }


}
