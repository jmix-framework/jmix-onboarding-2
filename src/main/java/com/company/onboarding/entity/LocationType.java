package com.company.onboarding.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;

public enum LocationType implements EnumClass<Integer> {

    OFFICE(10),
    COWORKING(20);

    private final Integer id;

    LocationType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static LocationType fromId(Integer id) {
        for (LocationType at : LocationType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}