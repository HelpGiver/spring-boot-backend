package com.example.helpgiver.objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
public class User {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String publicName;

    @Indexed(unique = true, name = "phone_number_index")
    private String phoneNumber;
    @Indexed(unique = true, name = "email_index")
    private String email;

    private String addressText;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE, name = "address_coordinates_index")
    private GeoJsonPoint addressCoordinates;

    private BigDecimal helpRadiusKm;

    private String riskGroup;

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public GeoJsonPoint getAddressCoordinates() {
        return addressCoordinates;
    }

    public void setAddressCoordinates(GeoJsonPoint addressCoordinates) {
        this.addressCoordinates = addressCoordinates;
    }

    public BigDecimal getHelpRadiusKm() {
        return helpRadiusKm;
    }

    public void setHelpRadiusKm(BigDecimal helpRadiusKm) {
        this.helpRadiusKm = helpRadiusKm;
    }

    public String getRiskGroup() {
        return riskGroup;
    }

    public void setRiskGroup(String riskGroup) {
        this.riskGroup = riskGroup;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", publicName='" + publicName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", addressText='" + addressText + '\'' +
                ", addressCoordinates=" + addressCoordinates +
                ", helpRadiusKm=" + helpRadiusKm +
                ", riskGroup='" + riskGroup + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
