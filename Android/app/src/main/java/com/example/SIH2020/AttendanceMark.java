package com.example.SIH2020;

public class AttendanceMark {

//    String name;
//    String latitude;
//    String longitude;
    String contactNumber;
    String status;

    public AttendanceMark(String phoneNumber, String status) {
        this.contactNumber = phoneNumber;
        this.status = status;
    }

    public String getcontactNumber() {
        return contactNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.contactNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
