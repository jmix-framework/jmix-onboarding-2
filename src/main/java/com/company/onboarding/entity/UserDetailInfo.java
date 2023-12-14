package com.company.onboarding.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JmixEntity
public class UserDetailInfo {

    @JmixGeneratedValue
    @JmixId
    private UUID id;

    @JmixProperty(mandatory = true)
    @NotNull
    private String address;

    @JmixProperty(mandatory = true)
    @NotNull
    private String state;

    @JmixProperty(mandatory = true)
    @NotNull
    private String city;

    @JmixProperty(mandatory = true)
    @NotNull
    private Integer aptNumber;

    public UserDetailInfo() {
    }

    public UserDetailInfo(String address, String state, String city, Integer aptNumber) {
        this.address = address;
        this.state = state;
        this.city = city;
        this.aptNumber = aptNumber;
    }

    public Integer getAptNumber() {
        return aptNumber;
    }

    public void setAptNumber(Integer aptNumber) {
        this.aptNumber = aptNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}