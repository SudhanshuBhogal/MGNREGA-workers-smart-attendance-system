package com.example.SIH2020;

public class Worker {
    String name;
    long contactNumber = 1234567891;
    public Worker(String name,long contactNumber)
    {
        this.name=name;
        this.contactNumber=contactNumber;
    }

    public Worker(String name)
    {
        this.name=name;
        this.contactNumber = 1234567891;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }
}
