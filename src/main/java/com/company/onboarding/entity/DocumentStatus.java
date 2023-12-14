package com.company.onboarding.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum DocumentStatus implements EnumClass<String> {

    COMPLETE("COMPLETE"),
    INCOMPLETE("INCOMPLETE");

    private final String id;

    DocumentStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static DocumentStatus fromId(String id) {
        for (DocumentStatus at : DocumentStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}