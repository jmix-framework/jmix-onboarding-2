package com.company.onboarding.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum Classification implements EnumClass<String> {

    PUBLIC("public"),
    RESTRICTED("restricted");

    private final String id;

    Classification(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Classification fromId(String id) {
        for (Classification at : Classification.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}