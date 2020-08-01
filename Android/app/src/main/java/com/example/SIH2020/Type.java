package com.example.SIH2020;

public class Type {

    private String contact;
    private String Email;
    private String Name;
    private String password;
    private String type;
    private String address;
    private double latitude;
    private double longitude;

    public Type()
    {

    }

    public Type(String name, String contact, String email, String password, String type) {
        this.contact = contact;
        this.Email = email;
        this.Name = name;
        this.password=password;
        this.type = type;
    }

    public Type(String name, String contact, String email, String password, String type,String address, double latitude, double longitude) {
        this.contact = contact;
        this.Email = email;
        this.Name = name;
        this.password=password;
        this.type = type;
        this.address=address;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return password;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return Email;
    }

    public void setAddress(String pass) {
        this.password = pass;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setName(String name) {
        Name = name;
    }

}
